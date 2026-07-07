package tech.thatgravyboat.skyblockapi.utils.regex.component

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import tech.thatgravyboat.skyblockapi.utils.extentions.stripColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import java.util.Optional

internal object ComponentUtils {

    fun substring(component: Component, start: Int, end: Int): Component {
        val components = mutableListOf<Component>()
        var current = 0

        component.visit({ style, part ->
            val length = part.stripColor().length
            if (current + length <= start) {
                current += length
            } else {
                val startIndex = start - current
                val endIndex = (end - current).coerceAtMost(length)
                components.add(Component.literal(part.substringIgnoreColorCodes(startIndex, endIndex)).setStyle(style))
                current += length
            }
            if (current >= end) Optional.of(Unit) else Optional.empty()
        }, Style.EMPTY)

        if (components.isEmpty()) return Component.empty()
        if (components.size == 1) return components[0]
        return Component.empty().apply {
            components.forEach(this::append)
        }
    }

    private fun String.substringIgnoreColorCodes(start: Int, end: Int): String {
        val builder = StringBuilder()
        var index = 0
        var current = 0
        var color = false
        while (current < end) {
            val char = this[index]
            if (char == '§') {
                color = true
                builder.append(char)
            } else if (color) {
                color = false
                builder.append(char)
            } else {
                if (current >= start) {
                    builder.append(char)
                }
                current++
            }
            index++
        }
        return builder.toString()
    }

    val Component.spans: List<Pair<String, Style>>
        get() {
            val output = mutableListOf<Pair<String, Style>>()
            this.visit(
                { style, content ->
                    output.add(content to style)
                    Optional.empty<Unit>()
                },
                Style.EMPTY,
            )
            return output
        }

    fun String.trimIgnoreColor(start: Boolean = true, end: Boolean = true): String {
        return this.tryTrimIgnoreColor(start, end).second
    }

    fun String.tryTrimIgnoreColor(start: Boolean = true, end: Boolean = true): Pair<Boolean, String> {
        var middle = this
        val prefix = StringBuilder()
        val suffix = StringBuilder()

        if (start) {
            var i = 0
            var include = false

            while (i < middle.length) {
                val char = middle[i]
                when {
                    include -> prefix.append(char)
                    char == ChatFormatting.PREFIX_CODE -> {
                        include = true
                        prefix.append(char)
                    }

                    !char.isWhitespace() -> break
                }
                i++
            }

            middle = middle.substring(i)
        }

        if (end) {
            var i = middle.length - 1
            var include = false

            while (i >= 0) {
                val char = middle[i]

                when {
                    include -> suffix.append(char)
                    i - 1 >= 0 && middle[i - 1] == ChatFormatting.PREFIX_CODE -> {
                        include = true
                        suffix.append(char)
                    }

                    !char.isWhitespace() -> break
                }
                i--
            }

            middle = middle.take(i + 1)
        }

        if (middle.isEmpty()) {
            return true to prefix.toString() + suffix.reverse().toString()
        }

        return false to prefix.toString() + middle + suffix.reverse().toString()
    }

    fun remove(source: Component, toRemove: Component): Component {
        val sourceText = source.stripped
        val toRemoveText = toRemove.stripped
        if (toRemoveText.isEmpty()) return source

        val startChar = sourceText.indexOf(toRemoveText)
        if (startChar == -1) return source
        val endChar = startChar + toRemoveText.length

        val newComponent = Component.empty()
        var currentOffset = 0

        source.visit(
            { style, part ->
                val partText = part.stripColor()
                val partLen = partText.length
                val partEnd = currentOffset + partLen

                when {
                    partEnd <= startChar -> {
                        newComponent.append(Component.literal(part).setStyle(style))
                    }

                    currentOffset >= endChar -> {
                        newComponent.append(Component.literal(part).setStyle(style))
                    }

                    else -> {
                        val relativeStart = (startChar - currentOffset).coerceAtLeast(0)
                        val relativeEnd = (endChar - currentOffset).coerceAtMost(partLen)

                        val leftSide = part.substring(0, relativeStart)
                        val rightSide = part.substring(relativeEnd)

                        if (leftSide.isNotEmpty()) newComponent.append(Component.literal(leftSide).setStyle(style))
                        if (rightSide.isNotEmpty()) newComponent.append(Component.literal(rightSide).setStyle(style))
                    }
                }

                currentOffset += partLen
                Optional.empty<Unit>()
            },
            Style.EMPTY,
        )

        return newComponent
    }
}
