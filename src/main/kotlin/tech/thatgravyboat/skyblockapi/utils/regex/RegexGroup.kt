package tech.thatgravyboat.skyblockapi.utils.regex

import org.intellij.lang.annotations.Language

public class RegexGroup(private val prefix: String) {

    public fun create(key: String, @Language("RegExp") regex: String): Regex = Regexes.create("$prefix.$key", regex)

    public fun createList(key: String, @Language("RegExp") vararg regexes: String): List<Regex> = Regexes.createList("$prefix.$key", *regexes)

    public fun group(subgroup: String): RegexGroup = RegexGroup("$prefix.$subgroup")

    public companion object {
        public val SCOREBOARD: RegexGroup = RegexGroup("scoreboard")
        public val TABLIST: RegexGroup = RegexGroup("tablist")
        public val TABLIST_WIDGET: RegexGroup = TABLIST.group("widget")
        public val CHAT: RegexGroup = RegexGroup("chat")
        public val ACTIONBAR_WIDGET: RegexGroup = RegexGroup("actionbar.widget")
        public val INVENTORY: RegexGroup = RegexGroup("inventory")
        public val ENTITY: RegexGroup = RegexGroup("entity")
    }
}
