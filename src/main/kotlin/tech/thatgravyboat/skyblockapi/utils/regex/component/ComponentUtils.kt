package tech.thatgravyboat.skyblockapi.utils.regex.component

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import tech.thatgravyboat.skyblockapi.utils.extentions.stripColor
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
}
