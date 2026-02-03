package tech.thatgravyboat.skyblockapi.utils.text

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FontDescription
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.contents.objects.AtlasSprite
import net.minecraft.network.chat.contents.objects.PlayerSprite
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.ResolvableProfile
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.hooks.RunnableClickEventHook
import tech.thatgravyboat.skyblockapi.impl.events.chat.setMessageId
import tech.thatgravyboat.skyblockapi.utils.regex.component.ComponentUtils
import tech.thatgravyboat.skyblockapi.utils.text.Text.asComponent
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.style
import java.net.URI
import java.util.*
import java.util.regex.Pattern


object CommonText {

    val NEWLINE: Component = "\n".asComponent()
    val HYPHEN: Component = "-".asComponent()
    val COMMA: Component = ",".asComponent()
    val SPACE: Component = " ".asComponent()
    val EMPTY: Component = "".asComponent()

    internal val PREFIX: Component = Text.of("[SkyBlockAPI] ", TextColor.YELLOW)
}

object Text {

    fun of(text: String, init: MutableComponent.() -> Unit = {}) = text.asComponent(init)
    fun of(init: MutableComponent.() -> Unit = {}) = "".asComponent(init)
    fun of(text: String, color: Int) = of(text) { this.color = color }
    fun translatable(text: String, init: MutableComponent.() -> Unit = {}): MutableComponent = Component.translatable(text).also(init)

    fun player(profile: ResolvableProfile, hat: Boolean = true, init: MutableComponent.() -> Unit = {}): MutableComponent {
        val spriteObj = PlayerSprite(profile, hat)
        return Component.`object`(spriteObj).also(init)
    }

    fun atlas(atlas: Identifier, sprite: Identifier, init: MutableComponent.() -> Unit = {}): MutableComponent {
        val spriteObj = AtlasSprite(atlas, sprite)
        return Component.`object`(spriteObj).also(init)
    }

    fun String.asComponent(init: MutableComponent.() -> Unit = {}): MutableComponent = Component.literal(this).also(init)

    @JvmOverloads
    fun multiline(vararg lines: Any?, init: MutableComponent.() -> Unit = {}) = join(*lines, separator = CommonText.NEWLINE, init = init)

    @JvmOverloads
    fun join(vararg components: Any?, separator: Component? = null, init: MutableComponent.() -> Unit = {}): MutableComponent {
        val result = Component.literal("")
        components.forEachIndexed { index, it ->
            when (it) {
                is Component -> result.append(it)
                is String -> result.append(it)
                is Char -> result.append(it.toString())
                is List<*> -> result.append(join(*it.toTypedArray(), separator = separator))
                null -> return@forEachIndexed
                else -> error("Unsupported type: ${it::class.simpleName}")
            }

            if (index < components.size - 1 && separator != null) {
                result.append(separator)
            }
        }
        return result.also(init)
    }

    fun Component.prefix(prefix: String): MutableComponent = join(prefix, this)
    fun Component.suffix(suffix: String): MutableComponent = join(this, suffix)
    fun Component.wrap(prefix: String, suffix: String) = this.prefix(prefix).suffix(suffix)
    fun Component.wrap(prefix: String, suffix: String, init: MutableComponent.() -> Unit) = this.prefix(prefix).suffix(suffix).apply(init)

    fun Component.send() = McClient.chat.addMessage(this)
    fun Component.send(id: String) = McClient.chat.setMessageId(id) {
        this.send()
    }

    internal fun debug(text: String = "", init: MutableComponent.() -> Unit = {}) =
        of("[SkyBlockAPI] $text") {
            this.color = TextColor.YELLOW
            init.invoke(this)
        }

    internal fun sendDebug(text: String = "", init: MutableComponent.() -> Unit = {}) = debug(text, init).send()
    internal fun Component.sendWithPrefix() = join(CommonText.PREFIX, this).send()
}

object TextProperties {

    private val STRIP_COLOR_PATTERN = Pattern.compile("(?i)\\u00A7.")

    val Component.width: Int get() = McFont.width(this)
    val Component.stripped: String get() = STRIP_COLOR_PATTERN.matcher(this.string).replaceAll("")
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
                    for (i in 1 until lines.lastIndex) {
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

    private fun <T> split(
        splits: List<T>,
        maxWidth: Int,
        calc: (T) -> Int,
        joiner: (List<T>) -> T,
    ): List<T> {
        val output = mutableListOf<T>()
        val current = mutableListOf<T>()
        var currentLength = 0
        for (split in splits) {
            val splitWidth = calc.invoke(split)
            if (currentLength + splitWidth > maxWidth) {
                output.add(joiner.invoke(current))
                current.clear()
                currentLength = 0
            }
            current.add(split)
            currentLength += splitWidth
        }

        if (current.isNotEmpty()) {
            output.add(joiner.invoke(current))
        }

        return output
    }

    fun Component.splitToWidth(separator: String, maxWidth: Int): List<Component> = split(
        this.split(separator),
        maxWidth,
        McFont::width,
    ) { Text.join(*it.toTypedArray(), Text.of(separator)) }

    fun String.splitToWidth(separator: String, maxWidth: Int): List<String> = split(
        this.split(separator),
        maxWidth,
        McFont::width,
    ) { it.joinToString(separator) }

    fun Component.substring(startIndex: Int): Component = this.substring(startIndex, this.stripped.length)
    fun Component.substring(startIndex: Int, endIndex: Int): Component = ComponentUtils.substring(this, startIndex, endIndex)
    fun Component.substring(range: IntRange): Component = this.substring(range.first, range.last)

    // TODO: optimize color codes to only add the necessary ones
    internal fun Component.toStringWithFormattingCodes(): String {
        val sb = StringBuilder()
        var last = Style.EMPTY

        this.visit(
            { style, text ->
                if (style != last) {
                    sb.append(ChatFormatting.RESET)
                    sb.appendStyle(style)
                    last = style
                }
                sb.append(text)
                Optional.empty<Unit>()
            },
            Style.EMPTY,
        )

        return sb.toString()
    }

    private fun StringBuilder.appendStyle(style: Style) {
        style.color?.let { color ->
            val value = color.value
            val formatting = ChatFormatting.entries.find { it.isColor && it.color == value } ?: ChatFormatting.RESET
            append(formatting)
        }

        if (style.isBold) append(ChatFormatting.BOLD)
        if (style.isItalic) append(ChatFormatting.ITALIC)
        if (style.isUnderlined) append(ChatFormatting.UNDERLINE)
        if (style.isStrikethrough) append(ChatFormatting.OBFUSCATED)
        if (style.isObfuscated) append(ChatFormatting.OBFUSCATED)
    }

}


internal fun MutableComponent.withFont(location: Identifier?): MutableComponent =
    this.style { if (location == null) withFont(null) else withFont(FontDescription.Resource(location)) }



internal fun Component.font(): Identifier? =
    (this.style.font as? FontDescription.Resource)?.id

internal fun Component.hover(): Component? = (this.style.hoverEvent as? HoverEvent.ShowText)?.value()

internal fun Component.command(): String? = (this.style.clickEvent as? ClickEvent.RunCommand)?.command()

internal fun Component.clipboard(): String? = (this.style.clickEvent as? ClickEvent.CopyToClipboard)?.value()

internal fun Component.suggest(): String? = (this.style.clickEvent as? ClickEvent.SuggestCommand)?.command()

internal fun Component.uri(): URI? = (this.style.clickEvent as? ClickEvent.OpenUrl)?.uri()

internal fun Component.url(): String? = this.uri()?.toString()

internal fun Component.color(): Int = this.style.color?.value ?: 0

internal fun Component.shadowColor(): Int? = this.style.shadowColor

internal fun Component.bold(): Boolean = this.style.isBold

internal fun Component.italic(): Boolean = this.style.isItalic

internal fun Component.underlined(): Boolean = this.style.isUnderlined

internal fun Component.strikethrough(): Boolean = this.style.isStrikethrough

internal fun Component.obfuscated(): Boolean = this.style.isObfuscated

object TextStyle {

    fun MutableComponent.style(init: Style.() -> Style): MutableComponent {
        this.withStyle { init.invoke(style) }
        return this
    }

    fun MutableComponent.onClick(runnable: () -> Unit): MutableComponent = this.style {
        val event = ClickEvent.SuggestCommand("SkyBlockAPI OnClick Action")
        @Suppress("KotlinConstantConditions")
        ((event as Any) as? RunnableClickEventHook)?.`skyblockapi$setRunnable` { runnable() }
        withClickEvent(event)
    }

    val Component.font: Identifier?
        get() = font()

    var MutableComponent.font: Identifier?
        get() = font()
        set(value) {
            this.withFont(value)
        }


    val Component.hover: Component?
        get() = hover()

    var MutableComponent.hover: Component?
        get() = hover()
        set(value) {
            this.style { withHoverEvent(value?.let { HoverEvent.ShowText(it) }) }
        }


    val Component.clipboard: String?
        get() = clipboard()

    var MutableComponent.clipboard: String?
        get() = clipboard()
        set(value) {
            this.style { withClickEvent(value?.let { ClickEvent.CopyToClipboard(it) }) }
        }


    val Component.command: String?
        get() = command()

    var MutableComponent.command: String?
        get() = command()
        set(value) {
            this.style { withClickEvent(value?.let { ClickEvent.RunCommand(it) }) }
        }


    val Component.suggest: String?
        get() = suggest()

    var MutableComponent.suggest: String?
        get() = suggest()
        set(value) {
            this.style { withClickEvent(value?.let { ClickEvent.SuggestCommand(it) }) }
        }


    val Component.uri: URI?
        get() = uri()

    var MutableComponent.uri: URI?
        get() = uri()
        set(value) {
            this.style { withClickEvent(value?.let { ClickEvent.OpenUrl(it) }) }
        }


    val Component.url: String?
        get() = url()

    var MutableComponent.url: String?
        get() = url()
        set(value) {
            this.uri = value?.let(URI::create)
        }


    val Component.color: Int
        get() = color()

    var MutableComponent.color: Int
        get() = color()
        set(value) {
            this.style { withColor(value) }
        }


    val Component.shadowColor: Int?
        get() = shadowColor()

    var MutableComponent.shadowColor: Int?
        get() = shadowColor()
        set(value) {
            this.style { this.withShadowColor(value ?: 0) }
        }


    val Component.bold: Boolean
        get() = bold()

    var MutableComponent.bold: Boolean
        get() = bold()
        set(value) {
            this.style { withBold(value) }
        }


    val Component.italic: Boolean
        get() = italic()

    var MutableComponent.italic: Boolean
        get() = italic()
        set(value) {
            this.style { withItalic(value) }
        }


    val Component.underlined: Boolean
        get() = underlined()

    var MutableComponent.underlined: Boolean
        get() = underlined()
        set(value) {
            this.style { withUnderlined(value) }
        }


    val Component.strikethrough: Boolean
        get() = strikethrough()

    var MutableComponent.strikethrough: Boolean
        get() = strikethrough()
        set(value) {
            this.style { withStrikethrough(value) }
        }

    val Component.obfuscated: Boolean
        get() = obfuscated()

    var MutableComponent.obfuscated: Boolean
        get() = obfuscated()
        set(value) {
            this.style { withObfuscated(value) }
        }
}

object TextBuilder {
    fun MutableComponent.append(like: ComponentLike): MutableComponent = this.append(like.toComponent())
    fun MutableComponent.append(init: MutableComponent.() -> Unit): MutableComponent = this.append(Text.of(init))
    fun MutableComponent.append(component: Component, init: MutableComponent.() -> Unit): MutableComponent = this.append(component.copy().apply(init))
    fun MutableComponent.append(text: String, init: MutableComponent.() -> Unit = {}): MutableComponent = this.append(text.asComponent(init))
    fun MutableComponent.append(number: Number, init: MutableComponent.() -> Unit = {}): MutableComponent = this.append(number.toString().asComponent(init))
    fun MutableComponent.append(boolean: Boolean, init: MutableComponent.() -> Unit = {}): MutableComponent = this.append(boolean.toString().asComponent(init))
    fun MutableComponent.append(text: String, color: Int): MutableComponent = this.append(text) { this.color = color }
}

object TextColor {

    const val BLACK = 0x000000
    const val DARK_BLUE = 0x0000AA
    const val DARK_GREEN = 0x00AA00
    const val DARK_AQUA = 0x00AAAA
    const val DARK_RED = 0xAA0000
    const val DARK_PURPLE = 0xAA00AA
    const val MAGENTA = DARK_PURPLE
    const val GOLD = 0xFFAA00
    const val ORANGE = GOLD
    const val GRAY = 0xAAAAAA
    const val DARK_GRAY = 0x555555
    const val BLUE = 0x5555FF
    const val GREEN = 0x55FF55
    const val AQUA = 0x55FFFF
    const val RED = 0xFF5555
    const val LIGHT_PURPLE = 0xFF55FF
    const val PINK = LIGHT_PURPLE
    const val YELLOW = 0xFFFF55
    const val WHITE = 0xFFFFFF

}
