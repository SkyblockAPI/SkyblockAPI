package tech.thatgravyboat.skyblockapi.utils.regex

import org.intellij.lang.annotations.Language
import tech.thatgravyboat.skyblockapi.helpers.McClient

object Regexes {
    private val usedKeys = mutableSetOf<String>()
    internal val regexes = mutableMapOf<String, Regex>()
    internal val regexLists = mutableMapOf<String, List<Regex>>()

    fun create(key: String, @Language("RegExp") regex: String): Regex {
        validateKey(key)

        val storedRegex = regexes[key]
        if (storedRegex != null) return storedRegex

        // We try to get the regex from regex lists if it fails from regexes, in case
        // in the future a key changes between a list or a single regex.
        // If we are trying to create the JSON, we ignore this
        if (!RegexData.isCreatingJson) {
            val listRegex = regexLists[key]?.firstOrNull()
            if (listRegex != null) return listRegex
        }

        val newRegex = Regex(regex)
        regexes[key] = newRegex
        return newRegex
    }

    fun createList(key: String, @Language("RegExp") vararg regex: String): List<Regex> {
        validateKey(key)
        val regexList = regexLists[key]
        if (regexList != null) return regexList

        // We try to get the regex from single regexes if it fails from regexes, in case
        // in the future a key changes between a list or a single regex
        // If we are trying to create the JSON, we ignore this
        if (!RegexData.isCreatingJson) {
            val normalRegex = regexes[key]
            if (normalRegex != null) return listOf(normalRegex)
        }

        val newRegexes = regex.map(::Regex)
        regexLists[key] = newRegexes
        return newRegexes
    }

    fun group(prefix: String) = RegexGroup(prefix)

    private val allowedChars: Set<Char> = buildSet {
        addAll('a'..'z')
        // TODO: maybe remove uppercase letters from allowed chars in keys?
        addAll('A'..'Z')
        addAll("._-".toList())
    }

    private fun validateKey(key: String) {
        if (!McClient.isDev) return
        require(key !in usedKeys) { "Regex Key '$key' is already in use" }
        require("@value" !in key) { "Regex key '$key' cannot contain '@value'" }
        require(key.split(".").none(String::isBlank)) { "Regex key '$key' has at least 2 '.' in a row." }
        require(key.all(allowedChars::contains)) { "Regex key '$key' contains illegal characters" }
        usedKeys += key
    }
}

