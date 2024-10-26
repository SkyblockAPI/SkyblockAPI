package tech.thatgravyboat.skyblockapi.utils.text

import net.minecraft.network.chat.*
import net.minecraft.util.StringUtil
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.impl.events.chat.setMessageId
import tech.thatgravyboat.skyblockapi.utils.text.Text.asComponent
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import java.util.*

object CommonText {

    val NEWLINE = "\n".asComponent()
    val HYPHEN = "-".asComponent()
    val SPACE = " ".asComponent()
    val EMPTY = "".asComponent()

}

object Text {

    fun of(text: String, init: MutableComponent.() -> Unit = {}) = text.asComponent(init)
    fun translatable(text: String, init: MutableComponent.() -> Unit = {}): MutableComponent = Component.translatable(text).also(init)
    fun String.asComponent(init: MutableComponent.() -> Unit = {}): MutableComponent = Component.literal(this).also(init)
    internal fun debug(text: String, init: MutableComponent.() -> Unit = {}) =
        of("[SkyBlockAPI] $text") {
            this.color = TextColor.YELLOW
            init.invoke(this)
        }

    fun multiline(vararg lines: Any?) = join(*lines, separator = CommonText.NEWLINE)
    fun join(vararg components: Any?, separator: MutableComponent? = null): MutableComponent {
        val result = Component.literal("")
        components.forEachIndexed { index, it ->
            when (it) {
                is Component -> result.append(it)
                is String -> result.append(it)
                is List<*> -> result.append(join(*it.toTypedArray(), separator = separator))
                null -> return@forEachIndexed
                else -> error("Unsupported type: ${it::class.simpleName}")
            }

            if (index < components.size - 1 && separator != null) {
                result.append(separator)
            }
        }
        return result
    }

    fun MutableComponent.prefix(prefix: String): MutableComponent = join(prefix, this)
    fun MutableComponent.suffix(suffix: String): MutableComponent = join(this, suffix)
    fun MutableComponent.wrap(prefix: String, suffix: String) = this.prefix(prefix).suffix(suffix)

    fun MutableComponent.send() = McClient.chat.addMessage(this)
    fun MutableComponent.send(id: String) = McClient.chat.setMessageId(id) {
        this.send()
    }
}

object TextProperties {

    val Component.width: Int get() = McFont.width(this)
    val Component.stripped: String get() = StringUtil.stripColor(this.string)
}

object TextUtils {

    fun Component.splitLines(): List<Component> = split("\n")

    fun Component.split(separator: String): List<Component> {
        val components = mutableListOf<Component>()
        var current = Component.empty()

        this.visit(
            { style, part ->
                val lines = part.split(separator)
                current.append(Component.literal(lines[0]).setStyle(style))
                if (lines.size > 1) {
                    components.add(current)
                    for (i in 2 until lines.lastIndex) {
                        components.add(Component.literal(lines[i]).setStyle(style))
                    }
                    current = Component.literal(lines.last()).setStyle(style)
                }
                Optional.empty<Unit>()
            },
            Style.EMPTY,
        )

        return components + current
    }

}

object TextStyle {

    fun MutableComponent.style(init: Style.() -> Style): MutableComponent {
        this.withStyle { init.invoke(style) }
        return this
    }

    var MutableComponent.hover: Component?
        get() = this.style.hoverEvent?.getValue(HoverEvent.Action.SHOW_TEXT)
        set(value) {
            this.style { withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, value)) }
        }

    var MutableComponent.command: String?
        get() = this.style.clickEvent?.takeIf { it.action == ClickEvent.Action.RUN_COMMAND }?.value
        set(value) {
            this.style { withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, value)) }
        }

    var MutableComponent.suggest: String?
        get() = this.style.clickEvent?.takeIf { it.action == ClickEvent.Action.SUGGEST_COMMAND }?.value
        set(value) {
            this.style { withClickEvent(ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, value)) }
        }

    var MutableComponent.url: String?
        get() = this.style.clickEvent?.takeIf { it.action == ClickEvent.Action.OPEN_URL }?.value
        set(value) {
            this.style { withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, value)) }
        }

    var MutableComponent.color: Int
        get() = this.style.color?.value ?: 0
        set(value) {
            this.style { withColor(value) }
        }

    var MutableComponent.bold: Boolean
        get() = this.style.isBold
        set(value) {
            this.style { withBold(value) }
        }
}

object TextColor {

    const val BLACK = 0x000000
    const val DARK_BLUE = 0x0000AA
    const val DARK_GREEN = 0x00AA00
    const val DARK_AQUA = 0x00AAAA
    const val DARK_RED = 0xAA0000
    const val DARK_PURPLE = 0xAA00AA
    const val GOLD = 0xFFAA00
    const val GRAY = 0xAAAAAA
    const val DARK_GRAY = 0x555555
    const val BLUE = 0x5555FF
    const val GREEN = 0x55FF55
    const val AQUA = 0x55FFFF
    const val RED = 0xFF5555
    const val LIGHT_PURPLE = 0xFF55FF
    const val YELLOW = 0xFFFF55
    const val WHITE = 0xFFFFFF

}
