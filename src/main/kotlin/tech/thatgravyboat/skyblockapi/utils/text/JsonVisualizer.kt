package tech.thatgravyboat.skyblockapi.utils.text

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

fun JsonElement.asComponent(): Component = JsonVisualizer().visualize(this)

class JsonVisualizer : AbstractDataVisualizer<JsonElement, JsonVisualizer.Token> {

    override val component: MutableComponent = Text.of()
    override var indentCount: Int = 0

    override fun visit(data: JsonElement) {
        when (data) {
            is JsonObject -> visitObject(data)
            is JsonArray -> visitArray(data)
            is JsonPrimitive -> visitPrimitive(data)
            is JsonNull -> {
                appendToken(Token.NULL)
            }

            else -> {
                append("<Unknown element type: $data>", -1)
            }
        }
    }

    override fun AbstractDataVisualizer.VisualizerToken.color(): Int = when (this) {
        Token.TRUE -> TextColor.DARK_GREEN
        Token.FALSE -> TextColor.RED
        Token.NULL -> TextColor.GRAY
        Token.KEY, Token.KEY_QUOTE -> TextColor.AQUA
        Token.NUMBER -> TextColor.GOLD
        Token.STRING, Token.STRING_QUOTE -> TextColor.GREEN
        else -> TextColor.WHITE
    }

    fun visitPrimitive(element: JsonPrimitive) = when {
        element.isBoolean -> appendToken(if (element.asBoolean) Token.TRUE else Token.FALSE)
        element.isNumber -> append(element.asNumber.toString(), Token.NUMBER)
        else -> appendToken(Token.STRING_QUOTE).append(element.asString, Token.STRING).appendToken(Token.STRING_QUOTE)
    }

    fun visitArray(element: JsonArray) {
        appendToken(Token.OPEN_ARRAY).line()
        indentCount += 1
        val iterator = element.iterator()
        while (iterator.hasNext()) {
            spaces().visit(iterator.next())
            if (iterator.hasNext()) {
                appendToken(Token.COMMA)
            }
            line()
        }
        indentCount -= 1
        spaces().appendToken(Token.CLOSE_ARRAY)
    }

    fun visitObject(element: JsonObject) {
        appendToken(Token.OPEN_OBJECT).line()
        indentCount += 1
        val iterator = element.entrySet().iterator()
        while (iterator.hasNext()) {
            val (key, value) = iterator.next()
            spaces().appendToken(Token.KEY_QUOTE).append(key, Token.KEY).appendToken(Token.KEY_QUOTE)
            appendToken(Token.COLON).appendToken(Token.SPACE)
            visit(value)
            if (iterator.hasNext()) {
                appendToken(Token.COMMA)
            }
            line()
        }
        indentCount -= 1
        spaces().appendToken(Token.CLOSE_OBJECT)
    }

    enum class Token(override val token: String?) : AbstractDataVisualizer.VisualizerToken {
        OPEN_OBJECT("{"),
        CLOSE_OBJECT("}"),
        OPEN_ARRAY("["),
        CLOSE_ARRAY("]"),
        COLON(":"),
        SPACE(" "),
        COMMA(","),
        KEY_QUOTE("\""),
        STRING_QUOTE("\""),
        TRUE("true"),
        FALSE("false"),
        NULL("null"),
        KEY(null),
        NUMBER(null),
        STRING(null),
    }
}
