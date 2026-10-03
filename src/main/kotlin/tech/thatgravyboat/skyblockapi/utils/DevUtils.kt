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
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import java.nio.file.Path
import java.util.*
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

sealed interface DebugEntry {
    val location: Identifier
    val description: String

    val devUtils: DevUtils

    fun canSetDefault(): Boolean = devUtils.supportsChangingDefault
    fun hasDescription(): Boolean {
        return description.isNotBlank() && description != location.toString() && description != location.path
    }
}

internal fun debugToggle(path: String, description: String = path, prefix: String = path): DebugToggle {
    return DebugToggle(SkyBlockAPI.id(path), description, SkyBlockApiDevUtils, prefix)
}

open class DebugToggle @JvmOverloads constructor(
    override val location: Identifier,
    override val description: String,
    override val devUtils: DevUtils,
    prefix: String = location.toString()
) : DebugEntry {
    open val prefix = Text.of(prefix, TextColor.DARK_PURPLE)
    var state: Boolean = true

    init {
        devUtils.register(this)
    }

    fun get() = state
    fun set(value: Boolean) = devUtils.setToggle(location, value)

    fun toggle() = devUtils.toggle(location)
    fun update() { state = devUtils.isOn(location) }

    fun getDefault() = devUtils.getDefaultToggle(location)
    fun toggleDefault() = devUtils.setToggleDefault(location, !getDefault())

    operator fun getValue(any: Nothing?, property: KProperty<*>): Boolean = get()

    operator fun getValue(any: Any?, property: KProperty<*>): Boolean = get()

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

open class DebugSelect<T : Any>(
    override val location: Identifier,
    override val description: String,
    override val devUtils: DevUtils,
    private var state: T? = null,
    private val toString: (T) -> String,
    private val states: List<T>,
) : DebugEntry {
    init {
        devUtils.register(this)
    }

    fun get(): T? = state
    fun set(value: T?) {
        this.state = value
        update()
    }
    fun hasState(): Boolean = this.state != null

    fun getByName(name: String?) = states.find { toString(it) == name }
    fun setByName(name: String?) = set(getByName(name))

    fun states(): List<String> = states.map(toString)

    fun stateName() = this.state?.let { toString(it) }
    fun update() { devUtils.selectStates[location] = stateName() }

    fun getDefault(): String? {
        if (!canSetDefault()) return null
        return devUtils.getDefaultSelect(location)
    }

    fun setDefault(value: String?) {
        if (!canSetDefault()) return
        devUtils.setSelectDefault(location, value)
    }

    fun hasDefault(): Boolean = getDefault() != null

    operator fun getValue(any: Nothing?, property: KProperty<*>): T? = get()
    operator fun getValue(any: Any?, property: KProperty<*>): T? = get()
}

@Module
internal object SkyBlockApiDevUtils : DevUtils() {
    override val commandName: String = "sbapi toggle"
    override fun send(component: MutableComponent) = component.sendWithPrefix()
    private val path: Path = loadPath()
    private lateinit var propertiesInstance: Properties
    val properties: Map<String, String> = loadFromProperties()

    override val supportsChangingDefault: Boolean = true
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

    private fun propertyKey(location: Identifier): String {
        return "${location.namespace}@${location.path}"
    }

    private fun saveProperties(location: Identifier, newDefault: String?, shouldRemove: (newDefault: String?) -> Boolean) {
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
        SkyBlockAPI.info("Changed properties file to set '$location' as '$newDefault'")
    }

    override fun getDefaultToggle(location: Identifier): Boolean {
        val key = propertyKey(location)
        return this.propertiesInstance.getProperty(key) == "true"
    }


    override fun setToggleDefault(location: Identifier, newDefaultValue: Boolean) {
        super.setToggleDefault(location, newDefaultValue)

        saveProperties(location, newDefaultValue.toString()) { newDefault -> newDefault != "true" }
    }

    override fun setSelectDefault(location: Identifier, newDefaultValue: String?) {
        super.setSelectDefault(location, newDefaultValue)

        saveProperties(location, newDefaultValue) { newDefault -> newDefault == null }
    }

    override fun getDefaultSelect(location: Identifier): String? {
        val debugSelect = select[location] ?: return null
        val key = propertyKey(location)
        val currentValue = this.propertiesInstance.getProperty(key) ?: return null
        if (currentValue !in debugSelect.states()) return null
        return currentValue
    }
    fun getInt(key: String, default: Int = 0): Int {
        return properties[key].parseFormattedInt(default)
    }

    fun getBoolean(key: String): Boolean {
        return properties[key] == "true"
    }

    private fun loadPath(): Path {
        val specifiedPath = System.getenv("tech_thatgravyboat_sbapi_debug_property_path") ?: System.getProperty("sbapi.property_path")
        return if (specifiedPath != null) Path(specifiedPath)
        else McClient.config.resolve("sbapi.properties")
    }

    private fun loadFromProperties(): Map<String, String> {
        val properties = Properties()
        if (path.notExists()) return emptyMap()
        path.reader(Charsets.UTF_8).use {
            properties.load(it)
        }
        this.propertiesInstance = properties
        val map = mutableMapOf<String, String>()
        properties.forEach { (key, value) ->
            val identifier = Identifiers.parseWithSeparator(key.toString(), '@') ?: return@forEach
            val string = value.toString()
            when (string) {
                "true" -> states[identifier] = true
                "false" -> states[identifier] = false
            }
            selectStates[identifier] = string
            map[key.toString()] = string
        }
        return map
    }

    @Subscription
    fun commandRegister(event: RegisterCommandsEvent) = super.onCommandRegister(event)
}

@Suppress("UNCHECKED_CAST")
private fun <V> Any.unsafe(): V = this as V

abstract class DevUtils {
    val states = mutableMapOf<Identifier, Boolean>()
    val selectStates = mutableMapOf<Identifier, String?>()

    val toggles = mutableListOf<DebugToggle>()
    val select = mutableMapOf<Identifier, DebugSelect<Any>>()

    val allDebugEntries: List<DebugEntry> get() = buildList {
        addAll(toggles)
        addAll(select.values)
    }

    /** whether this dev utils supports changing debug entries' default states when launching */
    open val supportsChangingDefault: Boolean = false
    open val namespace: String = SkyBlockAPI.NAMESPACE

    fun <T : Any> register(debugSelect: DebugSelect<T>) {
        select[debugSelect.location] = debugSelect.unsafe()
        if (supportsChangingDefault) debugSelect.setByName(selectStates[debugSelect.location])
    }

    fun register(debugToggle: DebugToggle) {
        states.putIfAbsent(debugToggle.location, false)
        toggles += debugToggle
        debugToggle.state = states[debugToggle.location] == true
    }

    fun toggle(location: Identifier) {
        states[location] = states[location]?.not() == true
        toggles.find { it.location == location }?.update()
    }

    fun isOn(location: Identifier) = states.getOrDefault(location, false)

    fun setToggle(location: Identifier, newValue: Boolean) {
        states[location] = newValue
        toggles.find { it.location == location }?.update()
    }
    fun setSelect(location: Identifier, newValue: String?) {
        val select = select[location] ?: return
        select.setByName(newValue)
    }

    /** If [supportsChangingDefault] is `true`, these should be implemented` */
    //region Default Management Functions
    open fun setToggleDefault(location: Identifier, newDefaultValue: Boolean) {
        //setToggle(location, newDefaultValue)
    }
    open fun getDefaultToggle(location: Identifier): Boolean = false

    open fun setSelectDefault(location: Identifier, newDefaultValue: String?) {
        //setSelect(location, newDefaultValue)
    }
    open fun getDefaultSelect(location: Identifier): String? = null
    //endregion

    fun onCommandRegister(event: RegisterCommandsEvent) {
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
                            append(argument.toString()) {
                                this.color = TextColor.GOLD
                            }
                            if (isOn(argument)) {
                                append(" on") { this.color = TextColor.GREEN }
                            } else {
                                append(" off") { this.color = TextColor.RED }
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
                            append(location.toString()) {
                                this.color = TextColor.GOLD
                            }
                            append(": ")

                            append(currentValue.toString()) { this.color = TextColor.RED }
                            append(" -> ")
                            append(nextValue.toString()) { this.color = TextColor.GREEN }
                        },
                    )
                }
            }
        }
    }

    abstract val commandName: String
    abstract fun send(component: MutableComponent)

    @JvmName("debugMessageString")
    @OptIn(ExperimentalContracts::class)
    inline fun <T : DebugToggle> debugString(toggle: T, provider: () -> String) {
        contract {
            callsInPlace(provider, InvocationKind.AT_MOST_ONCE)
        }
        debugMessage(toggle) { Text.of(provider()) }
    }

    @JvmName("debugMessageComponent")
    @OptIn(ExperimentalContracts::class)
    inline fun <T : DebugToggle> debugComponent(toggle: T, provider: () -> Component) {
        contract {
            callsInPlace(provider, InvocationKind.AT_MOST_ONCE)
        }
        debugMessage(toggle) { provider().copy() }
    }

    @OptIn(ExperimentalContracts::class)
    inline fun <T : DebugToggle> debugMessage(toggle: T, provider: () -> MutableComponent) {
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

    companion object {
        val allDevUtils = mutableListOf<DevUtils>()
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
