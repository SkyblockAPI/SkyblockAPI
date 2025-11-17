package tech.thatgravyboat.skyblockapi.utils.regex

import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.find
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match

private typealias RegexSwitchCase = Triple<Regex, List<String>, (Destructured) -> Unit>

class RegexSwitch {

    private val cases = mutableListOf<RegexSwitchCase>()

    fun case(regex: Regex, vararg groups: String = arrayOf(), action: (Destructured) -> Unit = {}) {
        cases.add(RegexSwitchCase(regex, groups.toList(), action))
    }

    internal fun check(input: String, checker: (Regex, String, Array<String>, (Destructured) -> Unit) -> Boolean): Boolean {
        for ((regex, groups, action) in cases) {
            if (checker(regex, input, groups.toTypedArray(), action)) {
                return true
            }
        }
        return false
    }

    internal fun anyCheck(input: List<String>, checker: (Regex, List<String>, Array<String>, (Destructured) -> Unit) -> Boolean): Boolean {
        for ((regex, groups, action) in cases) {
            if (checker(regex, input, groups.toTypedArray(), action)) {
                return true
            }
        }
        return false
    }
}

fun matchWhen(input: String, init: RegexSwitch.() -> Unit): Boolean = RegexSwitch().apply(init).check(input) { regex, input, groups, action ->
    regex.match(input = input, groups = groups, action = action)
}

fun anyMatchWhen(input: List<String>, init: RegexSwitch.() -> Unit): Boolean = RegexSwitch().apply(init).anyCheck(input) { regex, input, groups, action ->
    regex.anyMatch(input = input, groups = groups, action = action)
}

fun findWhen(input: String, init: RegexSwitch.() -> Unit): Boolean = RegexSwitch().apply(init).check(input) { regex, input, groups, action ->
    regex.find(input = input, groups = groups, action = action)
}
