package tech.thatgravyboat.skyblockapi.utils.regex.component

import net.minecraft.network.chat.Component
import org.intellij.lang.annotations.Language
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.contains
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

public class ComponentRegex(private val regex: Regex) {

    public constructor(@Language("RegExp") regex: String) : this(Regex(regex))

    public fun find(input: Component): ComponentMatchResult? = regex.find(input.stripped)?.let { ComponentMatchResult(input, it) }
    public fun match(input: Component): ComponentMatchResult? = regex.matchEntire(input.stripped)?.let { ComponentMatchResult(input, it) }

    public fun matches(input: Component) = matches(input.stripped)
    public fun contains(input: Component) = contains(input.stripped)
    public fun matches(input: String) = regex.matches(input)
    public fun contains(input: String) = regex.contains(input)

    public fun replace(component: Component, replacement: Component): Component = replace(component) { _ -> replacement }
    public fun replace(component: Component, transform: (ComponentMatchResult) -> Component): Component {
        var match = find(component) ?: return component

        var lastStart = 0
        val length = component.stripped.length
        val builder = Component.empty()

        do {
            builder.append(ComponentUtils.substring(component, lastStart, match.range().first))
            builder.append(transform(match))

            lastStart = match.range().last + 1
            match = match.next() ?: break
        } while (lastStart < length)

        if (lastStart < length) {
            builder.append(ComponentUtils.substring(component, lastStart, length))
        }

        return builder
    }

    public fun regex() = this.regex
}

public class Destructured internal constructor(private val match: ComponentMatchResult, private vararg val keys: String) {

    public val component: Component get() = match[0]!!

    private fun group(key: String): Component = match[key]!!

    public operator fun get(key: String): Component? = match[key]
    public operator fun get(index: Int): Component? = match[index]

    public operator fun component1(): Component = group(keys[0])
    public operator fun component2(): Component = group(keys[1])
    public operator fun component3(): Component = group(keys[2])
    public operator fun component4(): Component = group(keys[3])
    public operator fun component5(): Component = group(keys[4])
    public operator fun component6(): Component = group(keys[5])
    public operator fun component7(): Component = group(keys[6])
    public operator fun component8(): Component = group(keys[7])
    public operator fun component9(): Component = group(keys[8])
    public operator fun component10(): Component = group(keys[9])
}

public fun ComponentRegex.match(input: Component, vararg groups: String = arrayOf(), action: (Destructured) -> Unit = {}): Boolean =
    match(input)?.let { action(Destructured(it, *groups)) } != null

public fun <T> ComponentRegex.matchOrNull(input: Component, vararg groups: String = arrayOf(), action: (Destructured) -> T): T? =
    match(input)?.let { action(Destructured(it, *groups)) }

public fun List<ComponentRegex>.match(input: Component, vararg groups: String = arrayOf(), action: (Destructured) -> Unit = {}): Boolean =
    any { it.match(input = input, groups = groups, action = action) }

public fun ComponentRegex.anyMatch(input: List<Component>, vararg groups: String = arrayOf(), action: (Destructured) -> Unit = {}): Boolean = 
    input.any { match(it, groups = groups, action = action) }

public fun ComponentRegex.find(input: Component, vararg groups: String = arrayOf(), action: (Destructured) -> Unit = {}): Boolean =
    find(input)?.let { action(Destructured(it, *groups)) } != null

public fun <T> ComponentRegex.findOrNull(input: Component, vararg groups: String = arrayOf(), action: (Destructured) -> T): T? = 
    find(input)?.let { action(Destructured(it, *groups)) }

public fun ComponentRegex.findThenNull(input: Component, vararg groups: String = arrayOf(), action: (Destructured) -> Unit = {}): Unit? {
    val find = find(input) ?: return Unit
    action(Destructured(find, *groups))
    return null
}

public fun ComponentRegex.indexOfFirstMatch(input: List<Component>): Int = input.indexOfFirst(::matches)

public fun ComponentRegex.indexOfFirstFind(input: List<Component>): Int = input.indexOfFirst(::contains)

public fun List<ComponentRegex>.find(input: Component, vararg groups: String = arrayOf(), action: (Destructured) -> Unit = {}): Boolean = 
    any { it.find(input = input, groups = groups, action = action) }

public fun ComponentRegex.anyFound(input: List<Component>, vararg groups: String = arrayOf(), action: (Destructured) -> Unit = {}): Boolean =
    input.any { find(it, groups = groups, action = action) }

public fun Regex.toComponentRegex() = ComponentRegex(this)
