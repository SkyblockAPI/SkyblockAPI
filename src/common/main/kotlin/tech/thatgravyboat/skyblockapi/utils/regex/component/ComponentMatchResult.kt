package tech.thatgravyboat.skyblockapi.utils.regex.component

import net.minecraft.network.chat.Component

class ComponentMatchResult(private val component: Component, private val result: MatchResult) {

    private val match: Component = ComponentUtils.substring(component, result.range.first, result.range.last + 1)

    fun range(): IntRange = result.range
    fun value(): Component = match

    operator fun get(group: Int): Component? {
        val groups = result.groups
        if (group < 0 || group >= groups.size) return null
        return groups[group]?.range?.let {
            ComponentUtils.substring(component, it.first, it.last + 1)
        }
    }

    fun getPlain(group: Int): String? {
        val groups = result.groups
        if (group < 0 || group >= groups.size) return null
        return groups[group]?.value
    }

    operator fun get(group: String): Component? = result.groups[group]?.range?.let {
        ComponentUtils.substring(component, it.first, it.last + 1)
    }

    fun getPlain(group: String): String? = result.groups[group]?.value

    fun next(): ComponentMatchResult? {
        val next = result.next() ?: return null
        return ComponentMatchResult(component, next)
    }

}
