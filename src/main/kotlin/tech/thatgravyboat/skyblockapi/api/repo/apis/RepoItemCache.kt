package tech.thatgravyboat.skyblockapi.api.repo.apis

import com.google.gson.JsonObject
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import me.owdding.ktmodules.Module
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.CommandBuilder
import tech.thatgravyboat.skyblockapi.api.events.misc.LiteralCommandBuilder
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.impl.commands.GiveCommands
import tech.thatgravyboat.skyblockapi.impl.suggestion.ArgumentTypeSuggestionProvider
import tech.thatgravyboat.skyblockapi.impl.suggestion.LayeredSuggestionProvider
import tech.thatgravyboat.skyblockapi.utils.Logger
import tech.thatgravyboat.skyblockapi.utils.extentions.ItemStack
import tech.thatgravyboat.skyblockapi.utils.json.Json
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.sendWithPrefix
import java.util.concurrent.CompletableFuture
import kotlin.math.max
import kotlin.reflect.KMutableProperty1

abstract class RepoItemCache<K>(private val name: String) {

    private val cache: MutableMap<K, LazyItemStack?> = mutableMapOf()

    init {
        if (repos.contains(name)) {
            Logger.warn("RepoItemCache with name '$name' already exists. This may cause issues with cache invalidation.")
        }

        SkyBlockAPI.eventBus.register<ServerChangeEvent> {
            this.cache.values.forEach { it?.invalidate() }
        }
    }

    protected abstract fun create(key: K): LazyItemStack?

    fun getLazyItemStack(key: K): LazyItemStack? = ifInitialized {
        cache.getOrPut(key) { create(key) }
    }

    fun getItemStack(key: K): ItemStack? = getLazyItemStack(key)?.create()
    fun getItemStackOrDefault(key: K): ItemStack = getLazyItemStack(key)?.create() ?: ItemStack(Items.BARRIER) {
        this[DataComponents.ITEM_NAME] = Text.of("Could not find item for key '$key' in $name")
    }

    protected inline fun <Return> ifInitialized(action: () -> Return?): Return? = if (RepoAPI.isInitialized()) {
        action()
    } else {
        null
    }

    protected open fun clear() {
        cache.clear()
    }

    companion object {

        private val repos: MutableSet<String> = mutableSetOf()

        protected fun LazyItemStack(json: JsonObject?): LazyItemStack? = LazyItemStack.CODEC
            .parse(Json.ops, json ?: return null)
            .ifError { Logger.error(it.message()) }
            .result()
            .orElse(null)
    }
}

@Module
internal data object RepoQueryCommands {
    data class Schema<Query>(val creator: RepoItemCacheAsQuery<Query>) : MutableMap<String, SchemaEntry<Query, *>> by mutableMapOf(),
        RepoItemQuerySchema<Query> {
        init {
            creator.createSchema(this)
        }

        override fun <T> field(
            name: String,
            argument: ArgumentType<T>,
            optional: Boolean,
            setter: (Query, T) -> Unit,
            suggestionProvider: (SuggestionConsumer) -> Unit,
        ): RepoItemQuerySchema<Query> = apply {
            val previous = put(name, SchemaField(name, argument, optional, setter, suggestionProvider))
            if (previous != null) {
                throw UnsupportedOperationException("Duplicate key $name!")
            }
        }

        override fun flag(name: String, setter: (Query, Boolean) -> Unit): RepoItemQuerySchema<Query> = apply {
            val previous = put(name, SchemaFlag(name, setter))
            if (previous != null) {
                throw UnsupportedOperationException("Duplicate key $name!")
            }
        }
    }

    @Subscription
    context(event: RegisterSkyblockApiCommandsEvent)
    fun registerCommands() {
        event.register("dev give query") {
            schemas.forEach { (name, schema) ->
                createCommand(name, schema)
            }
        }
    }

    context(event: RegisterSkyblockApiCommandsEvent)
    fun <Query> createCommand(name: String, schema: Schema<Query>) {
        event.register("dev give item query " + name.lowercase().replace(Regex("\\s+"), "_")) {
            createUntyped(
                schema.values.filterIsInstance<SchemaField<Query, *>>().filterNot(SchemaField<*, *>::optional).sortedBy { (key) -> key }.iterator(),
            ) {
                thenCallback("query", SchemaArgument(schema.values.filterNot { it is SchemaField && !it.optional })) {
                    val query = schema.creator.factory()

                    fun <Type> addField(field: SchemaFieldArgument<Query, Type>) {
                        field.field.setter(query, field.value)
                    }

                    this.argument<Set<SchemaFieldArgument<Query, *>>>("query").forEach {
                        addField(it)
                    }

                    fun <Type> addRequiredField(schemaField: SchemaField<Query, Type>) {
                        val value = getArgument(schemaField.name, Any::class.java) as Type
                        schemaField.setter(query, value)
                    }

                    schema.values.filterIsInstance<SchemaField<Query, *>>().filterNot(SchemaField<*, *>::optional).forEach {
                        addRequiredField(it)
                    }

                    val result = schema.creator.getLazyItemStack(query) ?: return@thenCallback Text.of("No item matching $query found!").sendWithPrefix()

                    val item = result.create()

                    if (item.isEmpty) {
                        return@thenCallback Text.of("Item matching $query is empty!").sendWithPrefix()
                    }

                    GiveCommands.tryGive(item)
                }
            }
        }

    }

    fun <Query> CommandBuilder<out ArgumentBuilder<FabricClientCommandSource, *>>.createUntyped(
        iterator: Iterator<SchemaField<Query, *>>,
        callback: CommandBuilder<out ArgumentBuilder<FabricClientCommandSource, *>>.() -> Unit,
    ) {
        if (iterator.hasNext()) {
            this.createArgument(iterator, iterator.next(), callback)
        } else {
            callback(this)
        }
    }

    fun <Query, Type> CommandBuilder<out ArgumentBuilder<FabricClientCommandSource, *>>.createArgument(
        iterator: Iterator<SchemaField<Query, *>>,
        field: SchemaField<Query, Type>,
        callback: CommandBuilder<out ArgumentBuilder<FabricClientCommandSource, *>>.() -> Unit,
    ) {
        then(
            field.name, field.argument,
            LayeredSuggestionProvider(
                ArgumentTypeSuggestionProvider(field.argument),
                SuggestionProvider<FabricClientCommandSource> { _, builder ->
                    field.suggestionProvider(builder::suggest)
                    return@SuggestionProvider CompletableFuture()
                },
            ),
        ) {
            this.createUntyped(iterator, callback)
        }
    }

    val schemas: MutableMap<String, Schema<*>> = mutableMapOf()

    fun <Query> register(name: String, query: RepoItemCacheAsQuery<Query>) {
        schemas[name] = Schema(query)
    }
}


sealed interface SchemaEntry<Query, Type> {
    val name: String
    val setter: (Query, Type) -> Unit
}

data class SchemaField<Query, Type>(
    override val name: String,
    val argument: ArgumentType<Type>,
    val optional: Boolean,
    override val setter: (Query, Type) -> Unit,
    val suggestionProvider: (SuggestionConsumer) -> Unit,
) : SchemaEntry<Query, Type>

data class SchemaFlag<Query>(
    override val name: String,
    override val setter: (Query, Boolean) -> Unit,
) : SchemaEntry<Query, Boolean>

data class SchemaFieldArgument<Query, Type>(val field: SchemaEntry<Query, Type>, val value: Type)

private class SchemaArgument<Query>(val entries: List<SchemaEntry<Query, *>>) :
    ArgumentType<Set<SchemaFieldArgument<Query, *>>> {
    override fun parse(reader: StringReader): Set<SchemaFieldArgument<Query, *>> {
        val map = mutableSetOf<SchemaFieldArgument<Query, *>>()
        val minCursor = reader.cursor

        while (reader.canRead() && reader.peek() == '-') {
            val cursor = reader.cursor
            reader.skip()
            if (!reader.canRead()) break
            val filteredEntries = entries - map.map { it.field }.toSet()

            val beforeRead = reader.cursor
            val content = runCatching {
                reader.readStringUntil(' ')
            }.getOrElse {
                reader.cursor = reader.string.length + 1
                reader.string.substring(beforeRead)
            }
            reader.cursor -= 1

            val field = filteredEntries.find { it.name == content }

            fun <Type> parse(schema: SchemaField<Query, Type>) {
                val flagType = schema.argument
                reader.skipWhitespace()
                if (reader.canRead()) {
                    map.add(SchemaFieldArgument(schema, flagType.parse(reader)))
                }
            }

            if (field == null) {
                reader.cursor = reader.string.length
                return map
            }



            when (field) {
                is SchemaField<Query, *> -> parse(field)
                is SchemaFlag<Query> -> {
                    map.add(SchemaFieldArgument(field, true))
                }
            }


            if (reader.remainingLength >= 2 && reader.peek(1) == '-') reader.skipWhitespace()
        }

        reader.cursor = reader.string.length

        return map
    }

    private fun Iterable<SchemaField<Query, *>>.filterUnused(used: Iterable<SchemaField<Query, *>>) = this - used.toSet()

    override fun <S> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        val reader = StringReader(builder.input)
        reader.cursor = builder.start

        val consumedEntries: MutableSet<SchemaEntry<Query, *>> = mutableSetOf()
        while (reader.canRead() && reader.peek() == '-') {
            reader.skip()
            val cursor = reader.cursor
            val filteredFlags = entries - consumedEntries
            if (!reader.canRead()) {
                val offset = builder.createOffset(reader.cursor)
                filteredFlags.forEach {
                    offset.suggest(it.name)
                }
                return offset.buildFuture()
            }

            val beforeRead = reader.cursor
            val content = runCatching {
                reader.readStringUntil(' ')
            }.getOrElse {
                reader.cursor = beforeRead
                reader.string.substring(reader.cursor)
            }
            reader.cursor -= 1
            val entry = filteredFlags.find { it.name == content }

            if (entry == null) {
                reader.cursor = cursor
                val offset = builder.createOffset(reader.cursor)
                SharedSuggestionProvider.suggest(filteredFlags.map { it.name }, offset)
                return offset.buildFuture()
            }

            consumedEntries.add(entry)
            if (entry is SchemaField<Query, *>) {
                val flagType = entry.argument
                reader.skipWhitespace()
                val offset = builder.createOffset(reader.cursor)
                if (reader.canRead()) {
                    flagType.parse(reader)
                }
                if (reader.remainingLength >= 2 && reader.peek(1) == '-') {
                    reader.skipWhitespace()
                    continue
                }
                return flagType.listSuggestions(context, offset)
            }
            if (reader.remainingLength >= 2 && reader.peek(1) == '-') reader.skipWhitespace()
        }

        return super.listSuggestions(context, builder)
    }
}


sealed class RepoItemCacheAsQuery<Query>(name: String, internal val factory: () -> Query, internal val createSchema: RepoItemQuerySchema<Query>.() -> Unit) :
    RepoItemCache<Query>(name) {
    init {
        RepoQueryCommands.register(name, this)
    }

    fun getLazyItemStack(query: Query.() -> Unit): LazyItemStack? = this.getLazyItemStack(this.factory().apply(query))
    fun getItemStack(query: Query.() -> Unit): ItemStack? = getItemStack(this.factory().apply(query))
    fun getItemStackOrDefault(query: Query.() -> Unit): ItemStack = getItemStackOrDefault(this.factory().apply(query))
}

fun interface SuggestionConsumer {
    fun suggest(name: String)
    operator fun invoke(name: String) = suggest(name)
}

sealed interface RepoItemQuerySchema<Query> {
    fun <T> field(
        name: String,
        argument: ArgumentType<T>,
        optional: Boolean,
        setter: (Query, T) -> Unit,
        suggestionProvider: (SuggestionConsumer) -> Unit,
    ): RepoItemQuerySchema<Query>

    fun <T> optionalField(
        name: String,
        argument: ArgumentType<T>,
        setter: (Query, T) -> Unit,
        suggestionProvider: (SuggestionConsumer) -> Unit,
    ): RepoItemQuerySchema<Query> = field(name, argument, true, setter, suggestionProvider)

    fun <T> optionalField(name: String, argument: ArgumentType<T>, setter: (Query, T) -> Unit): RepoItemQuerySchema<Query> =
        field(name, argument, true, setter) {}

    fun <T> optionalField(name: String, argument: ArgumentType<T>, setter: (Query, T) -> Unit, suggestions: Iterable<String>): RepoItemQuerySchema<Query> =
        field(name, argument, true, setter) {
            suggestions.forEach(it::suggest)
        }

    fun <T> optionalField(
        name: String,
        argument: ArgumentType<T>,
        setter: KMutableProperty1<Query, T>,
        suggestionProvider: (SuggestionConsumer) -> Unit,
    ): RepoItemQuerySchema<Query> = field(name, argument, true, setter::set, suggestionProvider)

    fun <T> optionalField(name: String, argument: ArgumentType<T>, setter: KMutableProperty1<Query, T>): RepoItemQuerySchema<Query> =
        field(name, argument, true, setter::set) {}

    fun <T> optionalField(
        name: String,
        argument: ArgumentType<T>,
        setter: KMutableProperty1<Query, T>,
        suggestions: Iterable<String>,
    ): RepoItemQuerySchema<Query> = field(name, argument, true, setter::set) {
        suggestions.forEach(it::suggest)
    }

    fun <T> field(
        name: String,
        argument: ArgumentType<T>,
        setter: (Query, T) -> Unit,
        suggestionProvider: (SuggestionConsumer) -> Unit,
    ): RepoItemQuerySchema<Query> = field(name, argument, false, setter, suggestionProvider)

    fun <T> field(name: String, argument: ArgumentType<T>, setter: (Query, T) -> Unit): RepoItemQuerySchema<Query> = field(name, argument, false, setter) {}
    fun <T> field(name: String, argument: ArgumentType<T>, setter: (Query, T) -> Unit, suggestions: Iterable<String>): RepoItemQuerySchema<Query> =
        field(name, argument, false, setter) {
            suggestions.forEach(it::suggest)
        }

    fun <T> field(
        name: String,
        argument: ArgumentType<T>,
        setter: KMutableProperty1<Query, T>,
        suggestionProvider: (SuggestionConsumer) -> Unit,
    ): RepoItemQuerySchema<Query> = field(name, argument, false, setter::set, suggestionProvider)

    fun <T> field(name: String, argument: ArgumentType<T>, setter: KMutableProperty1<Query, T>): RepoItemQuerySchema<Query> =
        field(name, argument, false, setter::set) {}

    fun <T> field(name: String, argument: ArgumentType<T>, setter: KMutableProperty1<Query, T>, suggestions: Iterable<String>): RepoItemQuerySchema<Query> =
        field(name, argument, false, setter::set) {
            suggestions.forEach(it::suggest)
        }

    fun flag(name: String, setter: (Query, Boolean) -> Unit): RepoItemQuerySchema<Query>
    fun flag(name: String, setter: KMutableProperty1<Query, Boolean>): RepoItemQuerySchema<Query> = flag(name, setter::set)
}
