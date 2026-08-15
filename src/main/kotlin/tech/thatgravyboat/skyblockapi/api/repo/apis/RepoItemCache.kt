package tech.thatgravyboat.skyblockapi.api.repo.apis

import com.google.gson.JsonObject
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import me.owdding.ktmodules.Module
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.utils.Logger
import tech.thatgravyboat.skyblockapi.utils.extentions.ItemStack
import tech.thatgravyboat.skyblockapi.utils.json.Json
import tech.thatgravyboat.skyblockapi.utils.text.Text
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty0

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

        protected fun LazyItemStack(json: JsonObject): LazyItemStack? = LazyItemStack.CODEC
            .parse(Json.ops, json)
            .ifError { Logger.error(it.message()) }
            .result()
            .orElse(null)
    }
}

@Module
internal data object RepoQueryCommands {
    data class Schema<Query>(val creator: RepoItemCacheAsQuery<Query>) : MutableMap<String, SchemaField<Query, *>> by mutableMapOf(), RepoItemQuerySchema<Query> {
        init {
            creator.createSchema(this)
        }

        override fun <T> field(
            name: String,
            argument: ArgumentType<T>,
            optional: Boolean,
            setter: (Query, T) -> Unit,
        ): RepoItemQuerySchema<Query> = apply {
            val previous = put(name, SchemaField(name, argument, optional, setter))
            if (previous != null) {
                throw UnsupportedOperationException("Duplicate key $name!")
            }
        }
    }

    @Subscription
    context(event: RegisterSkyblockApiCommandsEvent)
    fun registerCommands() {
        event.register("dev give query") {
            schemas.forEach { (string, schema) ->
                then(string.lowercase().replace(Regex("\\s+"), "_")) {
                    thenCallback("meow") {
                        print("test")
                    }
                }
            }
        }
    }

    val schemas: MutableMap<String, Schema<*>> = mutableMapOf()

    data class SchemaField<Query, Type>(val name: String, val argument: ArgumentType<Type>, val optional: Boolean, val setter: (Query, Type) -> Unit)
    fun <Query> register(name: String, query: RepoItemCacheAsQuery<Query>) {
        schemas[name] = Schema(query)
    }

}

sealed class RepoItemCacheAsQuery<Query>(name: String, private val factory: () -> Query, internal val createSchema: RepoItemQuerySchema<Query>.() -> Unit) : RepoItemCache<Query>(name) {
    init {
        RepoQueryCommands.register(name, this)
    }

    fun getLazyItemStack(query: Query.() -> Unit): LazyItemStack? = this.getLazyItemStack(this.factory().apply(query))
    fun getItemStack(query: Query.() -> Unit): ItemStack? = getItemStack(this.factory().apply(query))
    fun getItemStackOrDefault(query: Query.() -> Unit): ItemStack = getItemStackOrDefault(this.factory().apply(query))
}

sealed interface RepoItemQuerySchema<Query> {
    fun <T> field(name: String, argument: ArgumentType<T>, optional: Boolean, setter: (Query, T) -> Unit): RepoItemQuerySchema<Query>
    fun <T> optionalField(name: String, argument: ArgumentType<T>, setter: (Query, T) -> Unit): RepoItemQuerySchema<Query> = field(name, argument, true, setter)
    fun <T> optionalField(name: String, argument: ArgumentType<T>, setter: KMutableProperty1<Query, T>): RepoItemQuerySchema<Query> = optionalField(name, argument, setter::set)
    fun <T> field(name: String, argument: ArgumentType<T>, setter: (Query, T) -> Unit): RepoItemQuerySchema<Query> = field(name, argument, false, setter)
    fun <T> field(name: String, argument: ArgumentType<T>, setter: KMutableProperty1<Query, T>): RepoItemQuerySchema<Query> = field(name, argument, setter::set)
    fun flag(name: String, setter: (Query, Boolean) -> Unit): RepoItemQuerySchema<Query> = optionalField(name, BoolArgumentType.bool(), setter)
    fun flag(name: String, setter: KMutableProperty1<Query, Boolean>): RepoItemQuerySchema<Query> = flag(name, setter::set)
}
