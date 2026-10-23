package tech.thatgravyboat.skyblockapi.utils.devutils

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.Identifier
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import tech.thatgravyboat.skyblockapi.utils.DevUtilBridge
import tech.thatgravyboat.skyblockapi.utils.command.VirtualResourceArgument
import tech.thatgravyboat.skyblockapi.utils.devutils.entries.DebugEntry
import tech.thatgravyboat.skyblockapi.utils.devutils.entries.DebugMultiSelect
import tech.thatgravyboat.skyblockapi.utils.devutils.entries.DebugSelect
import tech.thatgravyboat.skyblockapi.utils.devutils.entries.DebugToggle
import tech.thatgravyboat.skyblockapi.utils.extentions.mapValuesNotNull
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import java.nio.file.Path
import java.util.Properties
import java.util.concurrent.CompletableFuture
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.io.path.createParentDirectories
import kotlin.io.path.notExists
import kotlin.io.path.reader
import kotlin.io.path.writer

public abstract class DevUtils {
    protected val name: String = this::class.simpleName ?: this.toString()

    /** This only has the values set by default on launch */
    public val values: MutableMap<Identifier, String> = mutableMapOf()
    public val debugEntries: MutableMap<Identifier, DebugEntry> = mutableMapOf()

    private inline fun <reified Entry : DebugEntry> getEntriesOf(): Map<Identifier, Entry> = debugEntries.mapValuesNotNull { it.value as? Entry }

    public val debugToggles: Map<Identifier, DebugToggle> get() = getEntriesOf()
    public val debugSelects: Map<Identifier, DebugSelect<*>> get() = getEntriesOf()
    public val debugMultiSelects: Map<Identifier, DebugMultiSelect<*>> get() = getEntriesOf()

    /** whether this dev utils supports changing debug entries' default states when launching */
    public open val supportsChangingDefault: Boolean = false

    /** if [supportsChangingDefault] is `true`, this path should be set */
    protected open val path: Path? = null

    protected val propertiesInstance: Properties = Properties()
    protected open fun propertyKey(location: Identifier): String = "${location.namespace}@${location.path}"

    public open val namespace: String = SkyBlockAPI.NAMESPACE

    private fun registerEntry(debugEntry: DebugEntry) {
        val location = debugEntry.location
        val split = debugEntry.splitLocation()

        val anyOverlapping = debugEntries.values.find { other ->
            val otherSplit = other.splitLocation()
            val shared = minOf(split.size, otherSplit.size)
            split.take(shared) == otherSplit.take(shared)
        }
        if (anyOverlapping != null) McClient.throwInDev("Found overlapping debug entries: '$location' and '$anyOverlapping'")

        val previous = debugEntries.put(location, debugEntry)
        if (previous != null) McClient.throwInDev("Found duplicate debug entry with identifier '$location'")
    }

    public fun register(debugToggle: DebugToggle) {
        registerEntry(debugToggle)

        if (!supportsChangingDefault) return
        val storedValue = when (values[debugToggle.location]) {
            "true" -> true
            "false" -> false
            else -> return
        }
        debugToggle.set(storedValue)
    }

    public fun <T : Any> register(debugSelect: DebugSelect<T>) {
        registerEntry(debugSelect)

        if (!supportsChangingDefault) return
        val stored = values[debugSelect.location] ?: return
        val value = debugSelect.getByName(stored) ?: return
        debugSelect.set(value)
    }

    public fun <T : Any> register(debugMultiSelect: DebugMultiSelect<T>) {
        registerEntry(debugMultiSelect)
        // we don't load the values here so they can be lazily initialized
    }

    public inline fun <reified Entry : DebugEntry> getDebugEntry(location: Identifier): Entry? {
        return debugEntries[location] as? Entry
    }

    //region Default Management Functions
    public open fun setToggleDefault(location: Identifier, newDefaultValue: Boolean) {
        saveProperties(location, newDefaultValue.toString()) { newDefault -> newDefault != "true" }
    }

    public open fun getDefaultToggle(location: Identifier): Boolean {
        val key = propertyKey(location)
        return this.propertiesInstance.getProperty(key) == "true"
    }

    public open fun setSelectDefault(location: Identifier, newDefaultValue: String?) {
        saveProperties(location, newDefaultValue) { newDefault -> newDefault == null }
    }

    public open fun getDefaultSelect(location: Identifier): String? {
        val select = getDebugEntry<DebugSelect<*>>(location) ?: return null
        val key = propertyKey(location)
        val currentValue = this.propertiesInstance.getProperty(key) ?: return null
        if (currentValue !in select.states()) return null
        return currentValue
    }

    public open fun setMultiSelectDefault(location: Identifier, newDefaultValue: String?) {
        saveProperties(location, newDefaultValue) { newDefault -> newDefault.isNullOrBlank() }
    }

    public open fun getDefaultMultiSelect(location: Identifier): String? {
        val key = propertyKey(location)
        return this.propertiesInstance.getProperty(key)
    }

    protected open fun saveProperties(location: Identifier, newDefault: String?, shouldRemove: (newDefault: String?) -> Boolean) {
        val path = path ?: return SkyBlockAPI.info("Couldn't save $name properties because theres no path set")
        val properties = this.propertiesInstance
        val key = propertyKey(location)
        val currentValue = properties.getProperty(key)

        if (currentValue == newDefault) return
        if (shouldRemove(newDefault)) properties.remove(key)
        else properties.setProperty(key, newDefault)

        path.createParentDirectories()
        path.writer().use {
            properties.store(it, null)
        }
        SkyBlockAPI.info("Changed properties file $name to set '$location' as '$newDefault'")
    }

    protected open fun loadFromProperties(path: Path): Map<String, String> {
        val properties = this.propertiesInstance
        if (path.notExists()) return emptyMap()
        path.reader(Charsets.UTF_8).use {
            properties.load(it)
        }
        val map = mutableMapOf<String, String>()
        properties.forEach { (key, value) ->
            val identifier = Identifiers.parseWithSeparator(key.toString(), '@') ?: return@forEach
            val string = value.toString()
            map[key.toString()] = string
            values[identifier] = string
        }
        return map
    }
    //endregion

    protected inline fun <reified Entry : DebugEntry> CommandContext<*>.debugEntry(name: String): Entry {
        val location = argument<Identifier>(name)
        return getDebugEntry<Entry>(location) ?: error("Debug entry of type ${Entry::class.simpleName} with location $location not found")
    }

    public fun onCommandRegister(event: RegisterCommandsEvent) {
        event.register(commandName) {
            then(
                "toggle",
                VirtualResourceArgument(debugToggles.keys, namespace),
                DevToolSuggestionProvider(debugToggles.values, DebugToggle::location, DebugToggle::description),
            ) {

                callback {
                    val toggle = debugEntry<DebugToggle>("toggle")
                    toggle.toggle()
                    send(
                        Text.of("Toggled ") {
                            append(toggle.location.toString(), TextColor.GOLD)
                            if (toggle.get()) {
                                append(" on", TextColor.GREEN)
                            } else {
                                append(" off", TextColor.RED)
                            }
                        },
                    )
                }
            }
            then(
                "select",
                VirtualResourceArgument(debugSelects.keys, namespace),
                DevToolSuggestionProvider(debugSelects.values, DebugSelect<*>::location, DebugSelect<*>::description),
            ) {
                thenCallback(
                    "value",
                    StringArgumentType.greedyString(),
                    { context, builder ->
                        val select = context.debugEntry<DebugSelect<*>>("select")

                        select.states().forEach {
                            if (SharedSuggestionProvider.matchesSubStr(builder.remaining, it)) {
                                builder.suggest(it)
                            }
                        }

                        builder.buildFuture()
                    },
                ) {
                    val select = debugEntry<DebugSelect<*>>("select")
                    val value = argument<String>("value")

                    val currentValue = select.stateName()
                    select.setByName(value)
                    val nextValue = select.stateName()

                    send(
                        Text.of("Changed value for ") {
                            append(select.location.toString(), TextColor.GOLD)
                            append(": ")
                            append(currentValue.toString(), TextColor.RED)
                            append(" -> ")
                            append(nextValue.toString(), TextColor.GREEN)
                        },
                    )
                }
            }
            then(
                "multiselect",
                VirtualResourceArgument(debugMultiSelects.keys, namespace),
                DevToolSuggestionProvider(debugMultiSelects.values, DebugMultiSelect<*>::location, DebugMultiSelect<*>::description),
            ) {
                then("toggle") {
                    thenCallback(
                        "item",
                        StringArgumentType.greedyString(),
                        { context, builder ->
                            val multi = context.debugEntry<DebugMultiSelect<*>>("multiselect")

                            multi.states().forEach {
                                if (SharedSuggestionProvider.matchesSubStr(builder.remaining, it)) {
                                    builder.suggest(it)
                                }
                            }

                            builder.buildFuture()
                        },
                    ) {
                        val multi = debugEntry<DebugMultiSelect<*>>("multiselect")
                        val item = argument<String>("item")

                        multi.toggleByName(item)

                        send(
                            Text.of("Toggled ") {
                                append(item, TextColor.YELLOW)
                                append(" in ")
                                append(multi.location.toString(), TextColor.GOLD)
                                append(": ")
                                if (multi.isSelectedByName(item)) {
                                    append("Added", TextColor.GREEN)
                                } else {
                                    append("Removed", TextColor.RED)
                                }
                                append(" (Now: [${multi.serialized()}])")
                            },
                        )
                    }
                }
                thenCallback("clear") {
                    val multi = debugEntry<DebugMultiSelect<*>>("multiselect")
                    multi.set(emptyList())
                    send(
                        Text.of("Cleared selections for ") {
                            append(multi.location.toString(), TextColor.GOLD)
                        },
                    )
                }
                thenCallback("reset") {
                    val multi = debugEntry<DebugMultiSelect<*>>("multiselect")
                    multi.reset()
                    send(
                        Text.of("Reset ") {
                            append(multi.location.toString(), TextColor.GOLD)
                            append(" to default: [${multi.serialized()}]")
                        },
                    )
                }
            }
        }
    }

    public abstract val commandName: String
    public abstract fun send(component: MutableComponent)

    @JvmName("debugMessageString")
    @OptIn(ExperimentalContracts::class)
    public inline fun <T : DebugToggle> debugString(toggle: T, provider: () -> String) {
        contract {
            callsInPlace(provider, InvocationKind.AT_MOST_ONCE)
        }
        debugMessage(toggle) { Text.of(provider()) }
    }

    @JvmName("debugMessageComponent")
    @OptIn(ExperimentalContracts::class)
    public inline fun <T : DebugToggle> debugComponent(toggle: T, provider: () -> Component) {
        contract {
            callsInPlace(provider, InvocationKind.AT_MOST_ONCE)
        }
        debugMessage(toggle) { provider().copy() }
    }

    @OptIn(ExperimentalContracts::class)
    public inline fun <T : DebugToggle> debugMessage(toggle: T, provider: () -> MutableComponent) {
        contract {
            callsInPlace(provider, InvocationKind.AT_MOST_ONCE)
        }
        if (toggle.get()) {
            send(Text.join("<", toggle.prefix, "> ", provider().copy()))
        }
    }

    init {
        if (this !is DevUtilBridge) {
            allDevUtils.add(this)
        }
    }

    public companion object {
        public val allDevUtils: MutableList<DevUtils> = mutableListOf()
    }
}

private data class DevToolSuggestionProvider<T>(val utils: Iterable<T>, val location: T.() -> Identifier, val description: T.() -> String) :
    SuggestionProvider<FabricClientCommandSource> {
    override fun getSuggestions(context: CommandContext<FabricClientCommandSource>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        fun matches(arg: String): Boolean = SharedSuggestionProvider.matchesSubStr(builder.remaining.lowercase(), arg)

        utils.forEach {
            if (matches(it.location().toString().lowercase()) || matches(it.location().path.lowercase())) {
                builder.suggest(it.location().toString()) { it.description() }
            }
        }

        return builder.buildFuture()
    }
}
