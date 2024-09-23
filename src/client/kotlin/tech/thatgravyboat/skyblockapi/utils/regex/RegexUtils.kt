package tech.thatgravyboat.skyblockapi.utils.regex

object RegexUtils {

    fun Regex.match(input: CharSequence, vararg groups: String = arrayOf(), action: (Destructured) -> Unit): Boolean {
        val match = matchEntire(input)
        match?.let { action(Destructured(it, *groups)) }
        return match != null
    }

    fun List<Regex>.match(input: CharSequence, vararg groups: String = arrayOf(), action: (Destructured) -> Unit): Boolean {
        for (regex in this) {
            val match = regex.matchEntire(input)
            if (match != null) {
                action(Destructured(match, *groups))
                return true
            }
        }
        return false
    }

    fun Regex.find(input: CharSequence, vararg groups: String = arrayOf(), action: (Destructured) -> Unit): Boolean {
        val match = find(input)
        match?.let { action(Destructured(it, *groups)) }
        return match != null
    }

    fun List<Regex>.find(input: CharSequence, vararg groups: String = arrayOf(), action: (Destructured) -> Unit): Boolean {
        for (regex in this) {
            val match = regex.find(input)
            if (match != null) {
                action(Destructured(match, *groups))
                return true
            }
        }
        return false
    }
}

class Destructured internal constructor(private val match: MatchResult, private vararg val keys: String) {

    private fun group(key: String): String = match.groups[key]!!.value

    operator fun get(key: String): String = group(key)
    operator fun get(index: Int): String = match.groupValues[index]

    operator fun component1(): String = group(keys[0])
    operator fun component2(): String = group(keys[1])
    operator fun component3(): String = group(keys[2])
    operator fun component4(): String = group(keys[3])
    operator fun component5(): String = group(keys[4])
    operator fun component6(): String = group(keys[5])
    operator fun component7(): String = group(keys[6])
    operator fun component8(): String = group(keys[7])
    operator fun component9(): String = group(keys[8])
    operator fun component10(): String = group(keys[9])
    operator fun component11(): String = group(keys[10])
}
