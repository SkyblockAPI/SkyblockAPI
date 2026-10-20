package tech.thatgravyboat.skyblockapi.utils

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import me.owdding.ktmodules.Module
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.Identifier
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import tech.thatgravyboat.skyblockapi.utils.command.VirtualResourceArgument
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedInt
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.sendWithPrefix
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import java.nio.file.Path
import java.util.Properties
import java.util.concurrent.CompletableFuture
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.io.path.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.notExists
import kotlin.io.path.reader
import kotlin.io.path.writer
import kotlin.reflect.KProperty

public sealed interface DebugEntry {
    public val location: Identifier
    public val description: String

    public val devUtils: DevUtils

    public fun canSetDefault(): Boolean = devUtils.supportsChangingDefault
    public fun hasDescription(): Boolean {
        return description.isNotBlank() && description != location.toString() && description != location.path
    }
}

internal fun debugToggle(path: String, description: String = path, prefix: String = path): DebugToggle {
    return DebugToggle(SkyBlockAPI.id(path), description, SkyBlockApiDevUtils, prefix)
}

public open class DebugToggle @JvmOverloads constructor(
    override val location: Identifier,
    override val description: String,
    override val devUtils: DevUtils,
    prefix: String = location.toString(),
) : DebugEntry {
    public open val prefix: MutableComponent = Text.of(prefix, TextColor.DARK_PURPLE)
    public var state: Boolean = true

    init {
        devUtils.register(this)
    }

    public fun get(): Boolean = state
    public fun set(value: Boolean): Unit = devUtils.setToggle(location, value)

    public fun toggle(): Unit = devUtils.toggle(location)
    public fun update() {
        state = devUtils.isOn(location)
    }

    public fun getDefault(): Boolean = devUtils.getDefaultToggle(location)
    public fun toggleDefault(): Unit = devUtils.setToggleDefault(location, !getDefault())

    public operator fun getValue(any: Nothing?, property: KProperty<*>): Boolean = get()

    public operator fun getValue(any: Any?, property: KProperty<*>): Boolean = get()

}

internal fun <T : Any> debugSelect(
    path: String,
    description: String = path,
    initialState: T?,
    states: List<T>,
    toString: (T) -> String = { it.toString() },
): DebugSelect<T> {
    return DebugSelect(SkyBlockAPI.id(path), description, SkyBlockApiDevUtils, initialState, toString, states)
}

internal inline fun <reified T : Enum<T>> debugSelect(
    path: String,
    description: String = path,
    initialState: T? = null,
): DebugSelect<T> = debugSelect(path, description, initialState, T::class.java.enumConstants.toList())

public open class DebugSelect<T : Any>(
    override val location: Identifier,
    override val description: String,
    override val devUtils: DevUtils,
    initialState: T? = null,
    private val toString: (T) -> String,
    private val states: List<T>,
) : DebugEntry {
    private val defaultState: T? = initialState
    private var state: T? = initialState

    init {
        devUtils.register(this)
    }

    public fun get(): T? = state
    public fun set(value: T?) {
        this.state = fixValue(value)
        update()
    }

    //? if < 26.4 {
    @Deprecated("Use isDefaultState instead", ReplaceWith("isDefaultState()"))
    public fun hasState(): Boolean = isDefaultState()
    //?}

    public fun getByName(name: String?): T? = states.find { toString(it) == name }
    public fun setByName(name: String?): Unit = set(getByName(name))

    public fun states(): List<String> = states.map(toString)

    public fun stateName(): String? = this.state?.let(toString)
    public fun update() {
        devUtils.selectStates[location] = stateName()
    }

    public fun reset() {
        set(defaultState)
    }

    public fun getDefaultState(): T? = defaultState
    public fun isDefaultState(): Boolean = get() == this.defaultState
    public fun getDefaultStateName(): String? = this.defaultState?.let(toString)
    public fun isNullable(): Boolean = this.defaultState == null


    public fun getDefault(): String? {
        if (!canSetDefault()) return getDefaultStateName()
        return fixNameValue(devUtils.getDefaultSelect(location))
    }

    public fun setDefault(value: String?) {
        if (!canSetDefault()) return
        // This is to make sure you can't set it to null if it's not nullable
        devUtils.setSelectDefault(location, fixNameValue(value))
    }

    public fun hasDefault(): Boolean = getDefault() != getDefaultStateName()

    public operator fun getValue(any: Nothing?, property: KProperty<*>): T? = get()
    public operator fun getValue(any: Any?, property: KProperty<*>): T? = get()

    private fun fixNameValue(value: String?): String? = value ?: getDefaultStateName()

    // If the default value isn't null, we don't let the user use null
    private fun fixValue(value: T?): T? = value ?: defaultState
}

internal fun <T : Any> debugMultiSelect(
    path: String,
    description: String = path,
    initialState: Set<T> = emptySet(),
    toString: (T) -> String = { it.toString() },
    states: () -> List<T>,
): DebugMultiSelect<T> {
    return DebugMultiSelect(SkyBlockAPI.id(path), description, SkyBlockApiDevUtils, initialState, toString, states)
}

internal inline fun <reified T : Enum<T>> debugMultiSelect(
    path: String,
    description: String = path,
    initialState: Set<T> = emptySet(),
): DebugMultiSelect<T> = debugMultiSelect(path, description, initialState, { it.name }) { T::class.java.enumConstants.toList() }

public open class DebugMultiSelect<T : Any>(
    override val location: Identifier,
    override val description: String,
    override val devUtils: DevUtils,
    private val initialState: Set<T> = emptySet(),
    private val toString: (T) -> String,
    private val statesProvider: () -> List<T>,
) : DebugEntry {
    private val defaultState: Set<T> = initialState.toSet()

    private var isInitialized = false
    private val state: MutableSet<T> = mutableSetOf()

    private fun ensureInitialized() {
        if (isInitialized) return
        isInitialized = true
        state.addAll(initialState)

        val loadedState = devUtils.multiSelectStates[location]
        if (loadedState != null && canSetDefault()) {
            val items = loadedState.split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .mapNotNull { getByName(it) }
            state.clear()
            state.addAll(items)
        }
    }

    init {
        devUtils.register(this)
    }

    public fun get(): Set<T> {
        ensureInitialized()
        return state.toSet()
    }

    public fun set(values: Collection<T>) {
        ensureInitialized()
        state.clear()
        state.addAll(values.intersect(statesProvider().toSet()))
        update()
    }

    public fun isSelected(value: T): Boolean {
        ensureInitialized()
        return value in state
    }

    public fun isSelectedByName(name: String): Boolean = getByName(name)?.let { isSelected(it) } == true

    public fun toggle(value: T) {
        ensureInitialized()
        if (value in state) state.remove(value)
        else state.add(value)
        update()
    }

    public fun toggleByName(name: String) {
        getByName(name)?.let { toggle(it) }
    }

    public fun getByName(name: String?): T? = statesProvider().find { toString(it) == name }

    public fun states(): List<String> = statesProvider().map(toString)

    public fun setBySerialized(raw: String?) {
        ensureInitialized()
        if (raw == null) {
            reset()
            return
        }
        val items = raw.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .mapNotNull { getByName(it) }
        set(items)
    }

    public fun serialized(): String {
        ensureInitialized()
        return state.map(toString).sorted().joinToString(",")
    }

    public fun update() {
        devUtils.multiSelectStates[location] = serialized()
    }

    public fun reset() {
        set(defaultState)
    }

    public fun getDefaultState(): Set<T> = defaultState
    public fun isDefaultState(): Boolean = get() == defaultState
    public fun getDefaultSerialized(): String = defaultState.map(toString).sorted().joinToString(",")

    public fun getDefault(): String {
        if (!canSetDefault()) return getDefaultSerialized()
        return devUtils.getDefaultMultiSelect(location) ?: getDefaultSerialized()
    }

    public fun setDefault(serialized: String?) {
        if (!canSetDefault()) return
        devUtils.setMultiSelectDefault(location, serialized ?: getDefaultSerialized())
    }

    public fun hasDefault(): Boolean = getDefault() != getDefaultSerialized()

    public operator fun getValue(any: Nothing?, property: KProperty<*>): Set<T> = get()
    public operator fun getValue(any: Any?, property: KProperty<*>): Set<T> = get()
}

@Module
internal object SkyBlockApiDevUtils : DevUtils() {
    override val commandName: String = "sbapi toggle"
    override fun send(component: MutableComponent) = component.sendWithPrefix()

    override val supportsChangingDefault: Boolean = true
    override val path: Path = loadPath()
    val properties: Map<String, String> = loadFromProperties(path)

    override val namespace: String = SkyBlockAPI.NAMESPACE

    fun init() {
        toggles.forEach {
            val properties = System.getProperties()
            if (properties.containsKey(it.location.namespace + "_" + it.location.path)) {
                states[it.location] = true
                it.update()
            }
        }
    }

    // TODO: add debug int thing maybe?
    fun getInt(key: String, default: Int = 0): Int {
        return properties[key].parseFormattedInt(default)
    }

    private fun loadPath(): Path {
        val specifiedPath = System.getenv("tech_thatgravyboat_sbapi_debug_property_path") ?: System.getProperty("sbapi.property_path")
        return if (specifiedPath != null) Path(specifiedPath)
        else McClient.config.resolve("sbapi.properties")
    }

    @Subscription
    private fun commandRegister(event: RegisterCommandsEvent) = super.onCommandRegister(event)
}

@Suppress("UNCHECKED_CAST")
private fun <V> Any.unsafe(): V = this as V

public abstract class DevUtils {
    protected val name: String = this::class.simpleName ?: "Unknown"
    public val states: MutableMap<Identifier, Boolean> = mutableMapOf()
    public val selectStates: MutableMap<Identifier, String?> = mutableMapOf()
    public val multiSelectStates: MutableMap<Identifier, String?> = mutableMapOf()

    public val toggles: MutableList<DebugToggle> = mutableListOf()
    public val select: MutableMap<Identifier, DebugSelect<Any>> = mutableMapOf()
    public val multiSelect: MutableMap<Identifier, DebugMultiSelect<Any>> = mutableMapOf()

    public val allDebugEntries: List<DebugEntry>
        get() = buildList {
            addAll(toggles)
            addAll(select.values)
            addAll(multiSelect.values)
        }

    /** whether this dev utils supports changing debug entries' default states when launching */
    public open val supportsChangingDefault: Boolean = false
    /** if [supportsChangingDefault] is `true`, this path should be set */
    protected open val path: Path? = null

    protected val propertiesInstance: Properties = Properties()
    protected open fun propertyKey(location: Identifier): String = "${location.namespace}@${location.path}"

    public open val namespace: String = SkyBlockAPI.NAMESPACE

    public fun <T : Any> register(debugSelect: DebugSelect<T>) {
        select[debugSelect.location] = debugSelect.unsafe()
        val loadedState = selectStates[debugSelect.location] ?: return
        if (supportsChangingDefault) debugSelect.setByName(loadedState)
    }

    public fun <T : Any> register(debugMultiSelect: DebugMultiSelect<T>) {
        multiSelect[debugMultiSelect.location] = debugMultiSelect.unsafe()
    }

    public fun register(debugToggle: DebugToggle) {
        states.putIfAbsent(debugToggle.location, false)
        toggles += debugToggle
        debugToggle.state = states[debugToggle.location] == true
    }

    public fun toggle(location: Identifier) {
        states[location] = states[location]?.not() == true
        toggles.find { it.location == location }?.update()
    }

    public fun isOn(location: Identifier): Boolean = states.getOrDefault(location, false)

    public fun setToggle(location: Identifier, newValue: Boolean) {
        states[location] = newValue
        toggles.find { it.location == location }?.update()
    }

    public fun setSelect(location: Identifier, newValue: String?) {
        val select = select[location] ?: return
        select.setByName(newValue)
    }

    public fun toggleMultiSelectItem(location: Identifier, item: String) {
        val multi = multiSelect[location] ?: return
        multi.toggleByName(item)
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
        val debugSelect = select[location] ?: return null
        val key = propertyKey(location)
        val currentValue = this.propertiesInstance.getProperty(key) ?: return null
        if (currentValue !in debugSelect.states()) return null
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
            when (string) {
                "true" -> states[identifier] = true
                "false" -> states[identifier] = false
            }
            selectStates[identifier] = string
            multiSelectStates[identifier] = string
            map[key.toString()] = string
        }
        return map
    }
    //endregion

    public fun onCommandRegister(event: RegisterCommandsEvent) {
        event.register(commandName) {
            then(
                "toggle",
                VirtualResourceArgument(states.keys, namespace),
                DevToolSuggestionProvider(toggles, DebugToggle::location, DebugToggle::description),
            ) {
                callback {
                    val argument = this.getArgument("toggle", Identifier::class.java)
                    toggle(argument)
                    send(
                        Text.of("Toggled ") {
                            append(argument.toString(), TextColor.GOLD)
                            if (isOn(argument)) {
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
                VirtualResourceArgument(select.keys, namespace),
                DevToolSuggestionProvider(select.values, DebugSelect<*>::location, DebugSelect<*>::description),
            ) {
                thenCallback(
                    "value",
                    StringArgumentType.greedyString(),
                    { context, builder ->
                        val toggle = select[context.argument<Identifier>("select")] ?: return@thenCallback builder.buildFuture()

                        toggle.states().forEach {
                            if (SharedSuggestionProvider.matchesSubStr(builder.remaining, it)) {
                                builder.suggest(it)
                            }
                        }

                        builder.buildFuture()
                    },
                ) {
                    val location = argument<Identifier>("select")
                    val toggle = select[location] ?: return@thenCallback
                    val value = argument<String>("value")

                    val currentValue = toggle.stateName()
                    toggle.setByName(value)
                    val nextValue = toggle.stateName()

                    send(
                        Text.of("Changed value for ") {
                            append(location.toString(), TextColor.GOLD)
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
                VirtualResourceArgument(multiSelect.keys, namespace),
                DevToolSuggestionProvider(multiSelect.values, DebugMultiSelect<*>::location, DebugMultiSelect<*>::description),
            ) {
                then("toggle") {
                    thenCallback(
                        "item",
                        StringArgumentType.greedyString(),
                        { context, builder ->
                            val multi = multiSelect[context.argument<Identifier>("multiselect")] ?: return@thenCallback builder.buildFuture()

                            multi.states().forEach {
                                if (SharedSuggestionProvider.matchesSubStr(builder.remaining, it)) {
                                    builder.suggest(it)
                                }
                            }

                            builder.buildFuture()
                        },
                    ) {
                        val location = argument<Identifier>("multiselect")
                        val multi = multiSelect[location] ?: return@thenCallback
                        val item = argument<String>("item")

                        multi.toggleByName(item)

                        send(
                            Text.of("Toggled ") {
                                append(item, TextColor.YELLOW)
                                append(" in ")
                                append(location.toString(), TextColor.GOLD)
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
                    val location = argument<Identifier>("multiselect")
                    val multi = multiSelect[location] ?: return@thenCallback
                    multi.set(emptyList())
                    send(
                        Text.of("Cleared selections for ") {
                            append(location.toString(), TextColor.GOLD)
                        },
                    )
                }
                thenCallback("reset") {
                    val location = argument<Identifier>("multiselect")
                    val multi = multiSelect[location] ?: return@thenCallback
                    multi.reset()
                    send(
                        Text.of("Reset ") {
                            append(location.toString(), TextColor.GOLD)
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
        if (toggle.state) {
            send(Text.join("<", toggle.prefix, "> ", provider().copy()))
        }
    }

    init {
        allDevUtils.add(this)
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
