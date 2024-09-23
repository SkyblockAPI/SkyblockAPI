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
}

class Destructured internal constructor(private val match: MatchResult, private vararg val keys: String) {

    operator fun component1(): String = match.groups[keys[0]]!!.value
    operator fun component2(): String = match.groups[keys[1]]!!.value
    operator fun component3(): String = match.groups[keys[2]]!!.value
    operator fun component4(): String = match.groups[keys[3]]!!.value
    operator fun component5(): String = match.groups[keys[4]]!!.value
    operator fun component6(): String = match.groups[keys[5]]!!.value
    operator fun component7(): String = match.groups[keys[6]]!!.value
    operator fun component8(): String = match.groups[keys[7]]!!.value
    operator fun component9(): String = match.groups[keys[8]]!!.value
    operator fun component10(): String = match.groups[keys[9]]!!.value
    operator fun component11(): String = match.groups[keys[10]]!!.value
}
