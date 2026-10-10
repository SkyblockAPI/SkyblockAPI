package tech.thatgravyboat.skyblockapi.utils.regex

public object RegexUtils {

    public fun Regex.match(input: CharSequence, vararg groups: String = arrayOf(), action: (Destructured) -> Unit = {}): Boolean {
        val match = matchEntire(input)
        match?.let { action(Destructured(it, *groups)) }
        return match != null
    }

    public fun List<Regex>.match(input: CharSequence, vararg groups: String = arrayOf(), action: (Destructured) -> Unit = {}): Boolean {
        return any { it.match(input = input, groups = groups, action = action) }
    }

    public fun Regex.anyMatch(input: List<CharSequence>, vararg groups: String = arrayOf(), action: (Destructured) -> Unit = {}): Boolean {
        return input.any { match(it, groups = groups, action = action) }
    }

    public fun Regex.forEachMatch(input: List<CharSequence>, vararg groups: String = arrayOf(), action: (Destructured) -> Unit = {}): Boolean {
        var foundMatch = false
        input.forEach { if (match(it, groups = groups, action = action)) foundMatch = true }
        return foundMatch
    }

    public fun Regex.find(input: CharSequence, vararg groups: String = arrayOf(), action: (Destructured) -> Unit = {}): Boolean {
        val match = find(input)
        match?.let { action(Destructured(it, *groups)) }
        return match != null
    }

    public fun Regex.findGroup(input: CharSequence, group: String): String? {
        return find(input)?.let { Destructured(it, group).component1() }
    }

    public fun Regex.findGroup(input: List<String>, group: String): String? {
        return input.firstNotNullOfOrNull { findGroup(it, group) }
    }

    public fun Regex.findGroups(input: CharSequence, vararg groups: String = arrayOf()): Destructured? {
        return find(input)?.let { Destructured(it, *groups) }
    }

    public fun Regex.hasGroup(input: CharSequence, group: String): Boolean = findGroup(input, group) != null

    public fun Regex.hasGroup(input: List<String>, group: String): Boolean = findGroup(input, group) != null


    public fun Regex.contains(input: CharSequence): Boolean = containsMatchIn(input)

    public fun <T> Regex.matchOrNull(input: CharSequence, vararg groups: String = arrayOf(), action: (Destructured) -> T): T? {
        return matchEntire(input)?.let { action(Destructured(it, *groups)) }
    }

    public fun <T> Regex.findOrNull(input: CharSequence, vararg groups: String = arrayOf(), action: (Destructured) -> T): T? {
        return find(input)?.let { action(Destructured(it, *groups)) }
    }

    public fun Regex.findThenNull(input: CharSequence, vararg groups: String = arrayOf(), action: (Destructured) -> Unit = {}): Unit? {
        val match = find(input) ?: return Unit
        action(Destructured(match, *groups))
        return null
    }

    public fun Regex.indexOfFirstMatch(input: List<String>): Int = input.indexOfFirst(::matches)

    public fun Regex.indexOfFirstFind(input: List<String>): Int = input.indexOfFirst(::containsMatchIn)

    public fun List<Regex>.find(input: CharSequence, vararg groups: String = arrayOf(), action: (Destructured) -> Unit = {}): Boolean {
        return any { it.find(input = input, groups = groups, action = action) }
    }

    public fun Regex.anyFound(input: List<CharSequence>, vararg groups: String = arrayOf(), action: (Destructured) -> Unit = {}): Boolean {
        return input.any { find(it, groups = groups, action = action) }
    }

    public fun Regex.findAll(input: List<CharSequence>, vararg groups: String = arrayOf(), action: (Destructured) -> Unit = {}): Boolean {
        var globalFound = false
        input.forEach {
            val found = find(it, groups = groups, action = action)
            if (found) globalFound = true
        }
        return globalFound
    }

    public fun Regex.matchAll(input: List<CharSequence>, vararg groups: String = arrayOf(), action: (Destructured) -> Unit = {}): Boolean {
        var globalMatch = false
        input.forEach {
            val matched = match(it, groups = groups, action = action)
            if (matched) globalMatch = true
        }
        return globalMatch
    }
}

public class Destructured internal constructor(private val match: MatchResult, private vararg val keys: String) {

    public val string: String get() = match.groupValues[0]

    private fun group(key: String): String = match.groups[key]!!.value

    public operator fun get(key: String): String? = match.groups[key]?.value
    public operator fun get(index: Int): String? = match.groupValues.getOrNull(index)

    public operator fun component1(): String = group(keys[0])
    public operator fun component2(): String = group(keys[1])
    public operator fun component3(): String = group(keys[2])
    public operator fun component4(): String = group(keys[3])
    public operator fun component5(): String = group(keys[4])
    public operator fun component6(): String = group(keys[5])
    public operator fun component7(): String = group(keys[6])
    public operator fun component8(): String = group(keys[7])
    public operator fun component9(): String = group(keys[8])
    public operator fun component10(): String = group(keys[9])
    public operator fun component11(): String = group(keys[10])
}
