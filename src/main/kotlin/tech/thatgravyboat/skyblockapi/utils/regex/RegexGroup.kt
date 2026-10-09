package tech.thatgravyboat.skyblockapi.utils.regex

import org.intellij.lang.annotations.Language

public class RegexGroup(private val prefix: String) {

    public fun create(key: String, @Language("RegExp") regex: String) = Regexes.create("$prefix.$key", regex)

    public fun createList(key: String, @Language("RegExp") vararg regexes: String) = Regexes.createList("$prefix.$key", *regexes)

    public fun group(subgroup: String) = RegexGroup("$prefix.$subgroup")

    companion object {
        public val SCOREBOARD = RegexGroup("scoreboard")
        public val TABLIST = RegexGroup("tablist")
        public val TABLIST_WIDGET = TABLIST.group("widget")
        public val CHAT = RegexGroup("chat")
        public val ACTIONBAR_WIDGET = RegexGroup("actionbar.widget")
        public val INVENTORY = RegexGroup("inventory")
        public val ENTITY = RegexGroup("entity")
    }
}
