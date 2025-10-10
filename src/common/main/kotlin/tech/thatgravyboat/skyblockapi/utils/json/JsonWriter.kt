package tech.thatgravyboat.skyblockapi.utils.json

import com.google.gson.*
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.asComponent
import tech.thatgravyboat.skyblockapi.utils.text.Text.wrap
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

internal object JsonWriter {

    fun write(element: JsonElement?, depth: Int = 0, spaces: Int = 4, newlines: Boolean = true): Component {
        val nextDepth = if (newlines) depth + 1 else 1
        return when (element) {
            is JsonPrimitive if element.isBoolean -> Text.of(element.asBoolean.toString()) { this.color = TextColor.YELLOW }
            is JsonPrimitive if element.isNumber -> Text.of(element.asNumber.toString()) { this.color = TextColor.BLUE }
            is JsonPrimitive if element.isString -> element.asString.asComponent().wrap("\"", "\"").withColor(TextColor.GREEN)
            is JsonNull -> Text.of("null") { this.color = TextColor.DARK_GRAY }
            null -> Text.of("null") { this.color = TextColor.DARK_GRAY }
            is JsonArray -> {
                val opening = Text.of("[") { this.color = TextColor.WHITE }
                val closing = Text.of(if (newlines) "\n${" ".repeat(depth * spaces)}]" else "${" ".repeat(depth * spaces)}]") { this.color = TextColor.WHITE }
                val middle = element.map {
                    Text.join(
                        "\n".takeIf { newlines },
                        " ".repeat(nextDepth * spaces),
                        write(it, nextDepth, spaces, newlines)
                    )
                }
                Text.join(opening, Text.join(middle, separator = CommonText.COMMA), closing)
            }
            is JsonObject -> {
                val opening = Text.of("{") { this.color = TextColor.WHITE }
                val closing = Text.of(if (newlines) "\n${" ".repeat(depth * spaces)}}" else "${" ".repeat(depth * spaces)}}") { this.color = TextColor.WHITE }
                val middle = element.asMap().map { (key, value) ->
                    Text.join(
                        "\n".takeIf { newlines },
                        " ".repeat(nextDepth * spaces),
                        Text.of("\"$key\"") { this.color = TextColor.GREEN },
                        Text.of(": ") { this.color = TextColor.WHITE },
                        write(value, depth + 1, spaces, newlines)
                    )
                }
                Text.join(opening, Text.join(middle, separator = CommonText.COMMA), closing)
            }
            else -> error("Unknown element type: $element")
        }
    }
}
