package tech.thatgravyboat.skyblockapi.utils

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import me.owdding.ktmodules.Module
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.Identifier
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import tech.thatgravyboat.skyblockapi.utils.command.VirtualResourceArgument
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedInt
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.sendWithPrefix
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.io.path.Path
import kotlin.io.path.notExists
import kotlin.io.path.reader
import kotlin.reflect.KProperty

internal fun debugToggle(path: String, description: String = path): DebugToggle = DebugToggle(SkyBlockAPI.id(path), description, SkyBlockApiDevUtils)
internal fun <E : Enum<*>> debugSelect(path: String, description: String = path): DebugSelect<E> =
    DebugSelect(SkyBlockAPI.id(path), description, SkyBlockApiDevUtils)

abstract class DebugProperty<Type>(open val location: Identifier, open val description: String, val devUtils: DevUtils) {
    abstract operator fun getValue(any: Nothing?, property: KProperty<*>): Type?
    abstract operator fun getValue(any: Any?, property: KProperty<*>): Type?
}

open class DebugToggle(location: Identifier, description: String, devUtils: DevUtils) : DebugProperty<Boolean>(location, description, devUtils) {
    init {
        devUtils.register(this)
    }

    override operator fun getValue(any: Nothing?, property: KProperty<*>): Boolean = devUtils.isOn(location)
    override operator fun getValue(any: Any?, property: KProperty<*>): Boolean = devUtils.isOn(location)
}

open class DebugSelect<E : Enum<*>>(location: Identifier, description: String, devUtils: DevUtils) : DebugProperty<E>(location, description, devUtils) {
    init {
        devUtils.register(this)
    }

    override operator fun getValue(any: Nothing?, property: KProperty<*>): E? = devUtils.getState(location)
    override operator fun getValue(any: Any?, property: KProperty<*>): E? = devUtils.getState(location)
}

@Module
internal object SkyBlockApiDevUtils : DevUtils() {
    override val commandName: String = "sbapi toggle"
    override fun send(component: MutableComponent) = component.sendWithPrefix()
    val properties: Map<String, String> = loadFromProperties()

    fun getInt(key: String, default: Int = 0): Int {
        return properties[key].parseFormattedInt(default)
    }

    fun getBoolean(key: String): Boolean {
        return properties[key] == "true"
    }

    private fun loadFromProperties(): Map<String, String> {
        val properties = Properties()
        val path = System.getProperty("sbapi.property_path")?.let { Path(it) } ?: McClient.config.resolve("sbapi.properties")
        if (path.notExists()) return emptyMap()
        path.reader(Charsets.UTF_8).use {
            properties.load(it)
        }
        val map = mutableMapOf<String, String>()
        properties.forEach { (key, value) ->
            Identifiers.parseWithSeparator(key.toString(), '@')?.let {
                if (value.toString() == "true") {
                    states[it] = true
                }
            }
            map[key.toString()] = value.toString()
        }
        return map
    }
}

abstract class DevUtils {

    @RemoveNextVersion
    val toggles = mutableListOf<DebugToggle>()

    val states = mutableMapOf<Identifier, Boolean>()
    val selects = mutableMapOf<Identifier, Enum<*>?>()
    val debugs = mutableListOf<DebugProperty<*>>()

    fun register(debugProperty: DebugSelect<*>) {
        selects.putIfAbsent(debugProperty.location, null)
        debugs += debugProperty
    }

    fun register(debugToggle: DebugToggle) {
        states.putIfAbsent(debugToggle.location, false)
        toggles += debugToggle
    }

    fun toggle(location: Identifier) {
        states[location] = states[location]?.not() == true
    }
    fun isOn(location: Identifier) = states.getOrDefault(location, false)

    fun setState(location: Identifier, state: Enum<*>) {
        selects[location] = state
    }

    fun <E : Enum<*>> getState(location: Identifier): E? = selects[location] as E?

    @Subscription(inherited = true)
    fun onCommandRegister(event: RegisterCommandsEvent) {
        event.register(commandName) {
            then("location", VirtualResourceArgument(states.keys, SkyBlockAPI.NAMESPACE), DevToolSuggestionProvider(this@DevUtils)) {
                callback {
                    val argument = this.getArgument("location", Identifier::class.java)
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
        }
    }

    abstract val commandName: String
    abstract fun send(component: MutableComponent)
}

private data class DevToolSuggestionProvider(val utils: DevUtils) : SuggestionProvider<FabricClientCommandSource> {
    override fun getSuggestions(context: CommandContext<FabricClientCommandSource>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        fun matches(arg: String): Boolean = SharedSuggestionProvider.matchesSubStr(builder.remaining.lowercase(), arg)


        utils.toggles.forEach {
            if (matches(it.location.toString().lowercase()) || matches(it.location.path.lowercase())) {
                builder.suggest(it.location.toString()) { it.description }
            }
        }

        return builder.buildFuture()
    }
}
