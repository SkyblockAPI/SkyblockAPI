package tech.thatgravyboat.skyblockapi.utils.text

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.utils.extentions.associateByNotNull
import tech.thatgravyboat.skyblockapi.utils.regex.component.ComponentUtils
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import java.util.*
import net.minecraft.network.chat.TextColor as McTextColor

object TextUtils {

    fun Component.isEmpty(): Boolean = this.stripped.isEmpty()
    fun Component.isBlank(): Boolean = this.stripped.isBlank()

    /** Returns a copy of the component where all the leading and trailing lines that are blank have been removed */
    fun Component.trimLines(): Component {
        return Text.multiline(splitLines().dropWhile { it.isBlank() }.dropLastWhile { it.isBlank() })
    }

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

    private val colorTable = ChatFormatting.entries.associateByNotNull { McTextColor.fromLegacyFormat(it)?.value }

    private fun StringBuilder.appendStyle(style: Style) {
        style.color?.let { color ->
            append(colorTable[color.value] ?: ChatFormatting.RESET)
        }

        if (style.isBold) append(ChatFormatting.BOLD)
        if (style.isItalic) append(ChatFormatting.ITALIC)
        if (style.isUnderlined) append(ChatFormatting.UNDERLINE)
        if (style.isStrikethrough) append(ChatFormatting.OBFUSCATED)
        if (style.isObfuscated) append(ChatFormatting.OBFUSCATED)
    }

}

