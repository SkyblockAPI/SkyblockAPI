package tech.thatgravyboat.skyblockapi.utils.regex

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.intellij.lang.annotations.Language
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.http.Http
import tech.thatgravyboat.skyblockapi.utils.json.Json.isString
import tech.thatgravyboat.skyblockapi.utils.runCatchBlocking

private const val URL = ""

object Regexes {

    private val usedKeys = mutableSetOf<String>()
    private val regexes = mutableMapOf<String, Regex>()
    private val regexLists = mutableMapOf<String, List<Regex>>()

    fun create(key: String, @Language("RegExp") regex: String): Regex {
        validateKey(key)
        return regexes.getOrPut(key) {
            Regex(regex)
        }
    }

    fun createList(key: String, @Language("RegExp") vararg regex: String): List<Regex> {
        validateKey(key)
        return regexLists.getOrPut(key) {
            regex.map(::Regex).toList()
        }
    }

    fun group(prefix: String) = RegexGroup(prefix)

    private fun validateKey(key: String) {
        if (!McClient.isDev) return
        if (key in usedKeys) error("Regex Key $key is already in use")
        usedKeys += key
    }

    @JvmStatic
    internal fun load() {
        if (McClient.isDev) return
        runCatchBlocking {
            val result = Http.getResult<JsonObject>(URL)
            val json = result.getOrNull() ?: return@runCatchBlocking
            json.entrySet().forEach { (key, value) ->
                if (value is JsonArray) {
                    regexLists[key] = value.filter { it.isString }.map { it.asString }.map(::Regex).toList()
                } else if (value.isString) {
                    regexes[key] = Regex(value.asString)
                }
            }
        }
    }
}

