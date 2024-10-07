package tech.thatgravyboat.skyblockapi.utils.regex.component

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import tech.thatgravyboat.skyblockapi.utils.extentions.stripColor
import java.util.*

class ComponentMatchResult(component: Component, private val result: MatchResult) {

    private val match: Component = component.substring(result.range.first, result.range.last + 1)

    fun value(): Component = match

    operator fun get(group: Int): Component? {
        val groups = result.groups
        if (group < 0 || group >= groups.size) return null
        return groups[group]?.range?.let {
            match.substring(it.first, it.last + 1)
        }
    }

    operator fun get(group: String): Component? = result.groups[group]?.range?.let {
        match.substring(it.first, it.last + 1)
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

private fun Component.substring(start: Int, end: Int): Component {
    val components = mutableListOf<Component>()
    var current = 0

    this.visit({ style, part ->
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
    return Component.empty().apply {
        components.forEach(this::append)
    }
}
