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

    private val regexes = mutableMapOf<String, Regex>()
    private val regexLists = mutableMapOf<String, List<Regex>>()

    fun create(key: String, @Language("RegExp") regex: String) = regexes.getOrPut(key) {
        Regex(regex)
    }

    fun createList(key: String, @Language("RegExp") vararg regex: String) = regexLists.getOrPut(key) {
        regex.map(::Regex).toList()
    }

    fun group(prefix: String) = RegexGroup(prefix)

    @JvmStatic
    internal fun load() {
        if (McClient.isDev) return
        runCatchBlocking {
            val result = Http.getResult<JsonObject>(
                url = URL,
                errorFactory = ::RuntimeException,
            )
            loadJson(result.getOrNull() ?: return@runCatchBlocking)
        }
    }

    private fun loadJson(json: JsonObject, path: String = "") {
        json.entrySet().forEach { (key, value) ->
            val id = if (path.isEmpty()) key else "$path.$key"
            if (value is JsonObject) {
                loadJson(value, id)
            } else if (value is JsonArray) {
                regexLists[id] = value.filter { it.isString }.map { it.asString }.map(::Regex).toList()
            } else if (value.isString) {
                regexes[id] = Regex(value.asString)
            }
        }
    }
}

