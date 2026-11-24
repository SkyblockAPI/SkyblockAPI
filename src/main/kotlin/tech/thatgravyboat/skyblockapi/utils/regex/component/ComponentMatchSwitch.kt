package tech.thatgravyboat.skyblockapi.utils.regex.component

import net.minecraft.network.chat.Component

private typealias ComponentRegexSwitchCase = Triple<ComponentRegex, List<String>, (Destructured) -> Unit>

public class ComponentRegexSwitch {

    private val cases = mutableListOf<ComponentRegexSwitchCase>()

    public fun case(regex: ComponentRegex, vararg groups: String = arrayOf(), action: (Destructured) -> Unit = {}) {
        cases.add(ComponentRegexSwitchCase(regex, groups.toList(), action))
    }

    internal fun <T> check(input: T, checker: (ComponentRegex, T, Array<String>, (Destructured) -> Unit) -> Boolean): Boolean {
        for ((regex, groups, action) in cases) {
            if (checker(regex, input, groups.toTypedArray(), action)) {
                return true
            }
        }
        return false
    }
}

public fun componentMatchWhen(input: Component, init: ComponentRegexSwitch.() -> Unit): Boolean = ComponentRegexSwitch().apply(init).check(input) { regex, input, groups, action ->
    regex.match(input = input, groups = groups, action = action)
}

public fun anyComponentMatchWhen(input: List<Component>, init: ComponentRegexSwitch.() -> Unit): Boolean = ComponentRegexSwitch().apply(init).check(input) { regex, input, groups, action ->
    regex.anyMatch(input = input, groups = groups, action = action)
}

public fun componentFindWhen(input: Component, init: ComponentRegexSwitch.() -> Unit): Boolean = ComponentRegexSwitch().apply(init).check(input) { regex, input, groups, action ->
    regex.find(input = input, groups = groups, action = action)
}
