package tech.thatgravyboat.skyblockapi.utils.regex.component

import net.minecraft.network.chat.Component
import org.intellij.lang.annotations.Language
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

class ComponentRegex(private val regex: Regex) {

    constructor(@Language("RegExp") regex: String) : this(Regex(regex))

    fun find(input: Component): ComponentMatchResult? = regex.find(input.stripped)?.let { ComponentMatchResult(input, it) }

    fun match(input: Component): ComponentMatchResult? = regex.matchEntire(input.stripped)?.let { ComponentMatchResult(input, it) }

    fun matches(input: Component) = matches(input.stripped)
    fun contains(input: Component) = contains(input.stripped)
    fun matches(input: String) = regex.matches(input)
    fun contains(input: String) = regex.containsMatchIn(input)

    fun regex() = this.regex
}

class Destructured internal constructor(private val match: ComponentMatchResult, private vararg val keys: String) {

    val component: Component get() = match[0]!!

    private fun group(key: String): Component = match[key]!!

    operator fun get(key: String): Component? = match[key]
    operator fun get(index: Int): Component? = match[index]

    operator fun component1(): Component = group(keys[0])
    operator fun component2(): Component = group(keys[1])
    operator fun component3(): Component = group(keys[2])
    operator fun component4(): Component = group(keys[3])
    operator fun component5(): Component = group(keys[4])
    operator fun component6(): Component = group(keys[5])
    operator fun component7(): Component = group(keys[6])
    operator fun component8(): Component = group(keys[7])
    operator fun component9(): Component = group(keys[8])
    operator fun component10(): Component = group(keys[9])
}

fun ComponentRegex.match(input: Component, vararg groups: String = arrayOf(), action: (Destructured) -> Unit = {}): Boolean =
    match(input)?.let { action(Destructured(it, *groups)) } != null

fun <T> ComponentRegex.matchOrNull(input: Component, vararg groups: String = arrayOf(), action: (Destructured) -> T): T? =
    match(input)?.let { action(Destructured(it, *groups)) }

fun List<ComponentRegex>.match(input: Component, vararg groups: String = arrayOf(), action: (Destructured) -> Unit = {}): Boolean =
    any { it.match(input = input, groups = groups, action = action) }

fun ComponentRegex.anyMatch(input: List<Component>, vararg groups: String = arrayOf(), action: (Destructured) -> Unit = {}): Boolean = 
    input.any { match(it, groups = groups, action = action) }

fun ComponentRegex.find(input: Component, vararg groups: String = arrayOf(), action: (Destructured) -> Unit = {}): Boolean =
    find(input)?.let { action(Destructured(it, *groups)) } != null

fun <T> ComponentRegex.findOrNull(input: Component, vararg groups: String = arrayOf(), action: (Destructured) -> T): T? = 
    find(input)?.let { action(Destructured(it, *groups)) }

fun List<ComponentRegex>.find(input: Component, vararg groups: String = arrayOf(), action: (Destructured) -> Unit = {}): Boolean = 
    any { it.find(input = input, groups = groups, action = action) }

fun ComponentRegex.anyFound(input: List<Component>, vararg groups: String = arrayOf(), action: (Destructured) -> Unit = {}): Boolean =
    input.any { find(it, groups = groups, action = action) }
