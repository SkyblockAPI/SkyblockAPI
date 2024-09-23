package tech.thatgravyboat.skyblockapi.utils.text

import net.minecraft.network.chat.*
import net.minecraft.util.StringUtil
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.Text.asComponent

object CommonText {

    val NEWLINE = "\n".asComponent()
    val HYPHEN = "-".asComponent()
    val SPACE = " ".asComponent()
    val EMPTY = "".asComponent()

}

object Text {

    fun of(text: String, init: MutableComponent.() -> Unit = {}) = text.asComponent(init)
    fun String.asComponent(init: MutableComponent.() -> Unit = {}) = Component.literal(this).also(init)

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

    fun MutableComponent.send() = McClient.self.gui.chat.addMessage(this)
}

object TextProperties {

    val Component.width: Int
        get() = McClient.self.font?.width(this) ?: 0

    val Component.stripped: String
        get() = StringUtil.stripColor(this.string)
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
}
