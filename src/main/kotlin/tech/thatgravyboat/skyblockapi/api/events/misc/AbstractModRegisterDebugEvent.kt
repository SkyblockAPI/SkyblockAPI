package tech.thatgravyboat.skyblockapi.api.events.misc

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.Text.wrap
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.clipboard
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.time.Instant
import kotlin.time.toJavaInstant

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
                append(format(value))

                if (description != null) {
                    hover = description
                }

                clipboard = copyValue ?: value.toString()
            },
        )
    }

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss")
    private fun Instant.toReadableString(zoneId: ZoneId = ZoneOffset.systemDefault()): String {
        return dateTimeFormatter.format(LocalDateTime.ofInstant(this.toJavaInstant(), zoneId))
    }

    fun <T> format(value: T?): Component = when (value) {
        null -> Text.of("<null>", TextColor.ORANGE)
        is Iterable<*> -> Text.join(value.map {
            format(it)
        }).wrap("[", "]") {
            this.color = TextColor.GRAY
        }
        is Array<*> -> Text.join(value.map {
            format(it)
        }).wrap("[", "]") {
            this.color = TextColor.GRAY
        }
        is Boolean -> Text.of(value.toString(), if (value) TextColor.GREEN else TextColor.RED)
        is String -> Text.join('"', value, '"') {
            color = TextColor.DARK_GREEN
        }
        is Number -> Text.of(value.toString()) {
            color = TextColor.AQUA
            append(
                when (value) {
                    is Double -> "d"
                    is Long -> "L"
                    is Float -> "f"
                    is Short -> "s"
                    is Byte -> "b"
                    is Int -> ""
                    else -> " - ${value.javaClass.simpleName}"
                },
            )
        }
        is Instant -> Text.of(value.toReadableString(ZoneOffset.UTC))
        is Component -> value
        else -> Text.of(value.toString(), TextColor.YELLOW)
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
        append("\n")
    }
}
