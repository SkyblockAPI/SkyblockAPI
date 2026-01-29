package tech.thatgravyboat.skyblockapi.api.events.misc

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.clipboard
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover

abstract class AbstractModRegisterDebugEvent(val prefix: Component, val withDebug: Boolean = false, val base: AbstractModRegisterCommandsEvent) :
    SkyBlockEvent() {

    fun register(name: String, commandName: String = name.lowercase().replace(" ", "_"), init: DebugBuilder.() -> Unit) = register(Text.of(name), commandName, init)
    fun register(name: Component, commandName: String, init: DebugBuilder.() -> Unit) {
        base.registerWithCallback(name(commandName)) {
            DebugBuilder(prefix, name).apply(init).build().send()
        }
    }

    fun name(name: String) = if (withDebug) "debug $name" else name
}

internal class RegisterSkyblockApiDebugEvent(base: RegisterSkyblockApiCommandsEvent) :
    AbstractModRegisterDebugEvent(Text.of("[SkyblockAPI]", TextColor.YELLOW), false, base)

open class DebugBuilder(private val prefix: Component, private val name: Component) {
    private val fields: MutableList<Component> = mutableListOf()

    fun <T> field(field: String, value: T?, description: Component? = null, copyValue: String? = null) {
        fields.add(
            Text.of {
                append(field)
                append(": ")
                if (value == null) {
                    append("<null>", TextColor.ORANGE)
                } else {
                    append(value.toString()) {
                        color = when (value) {
                            is Boolean if value -> TextColor.GREEN
                            is Boolean -> TextColor.RED
                            else -> TextColor.YELLOW
                        }
                    }
                }

                if (description != null) {
                    hover = description
                }

                clipboard = copyValue ?: copyValue.toString()
            },
        )
    }

    fun build(): Component = Text.of {
        append(prefix)
        append(" ")
        append(name)
        append("\n")
        append("\n")
        append(
            Text.multiline(fields) {
                color = TextColor.GRAY
            },
        )
    }
}
