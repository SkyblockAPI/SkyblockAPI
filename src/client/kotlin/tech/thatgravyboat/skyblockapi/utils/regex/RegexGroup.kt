package tech.thatgravyboat.skyblockapi.utils.regex

import org.intellij.lang.annotations.Language

class RegexGroup(private val prefix: String) {

    fun create(key: String, @Language("RegExp") regex: String) = Regexes.create("$prefix.$key", regex)

    fun createList(key: String, @Language("RegExp") vararg regex: String) = Regexes.createList("$prefix.$key", *regex)

    fun group(subgroup: String) = RegexGroup("$prefix.$subgroup")
}
