package tech.thatgravyboat.skyblockapi.utils.regex

import org.intellij.lang.annotations.Language

class RegexGroup(private val prefix: String) {

    fun create(key: String, @Language("RegExp") regex: String) = Regexes.create("$prefix.$key", regex)

    fun createList(key: String, @Language("RegExp") vararg regexes: String) = Regexes.createList("$prefix.$key", *regexes)

    fun group(subgroup: String) = RegexGroup("$prefix.$subgroup")

    companion object {
        val SCOREBOARD = RegexGroup("scoreboard")
        val TABLIST = RegexGroup("tablist")
        val TABLIST_WIDGET = TABLIST.group("widget")
        val CHAT = RegexGroup("chat")
        val ACTIONBAR_WIDGET = RegexGroup("actionbar.widget")
    }
}
