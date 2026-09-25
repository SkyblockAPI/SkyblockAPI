package tech.thatgravyboat.skyblockapi.utils.regex

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import me.owdding.ktmodules.Module
import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.extentions.asString
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.http.Http
import tech.thatgravyboat.skyblockapi.utils.json.Json.isString
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.json.JsonArrayBuilder
import tech.thatgravyboat.skyblockapi.utils.json.JsonObjectBuilder
import tech.thatgravyboat.skyblockapi.utils.runCatchBlocking
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import kotlin.collections.mapNotNull
import kotlin.collections.set
import kotlin.io.path.isRegularFile
import kotlin.io.path.readText
import kotlin.io.path.writeText


private const val URL = ""
private const val FILE_PATH = "regexes.json"

@Module
internal object RegexData {

    enum class RegexSource {
        CODE,
        REMOTE,
        LOCAL_FILE,
    }

    val isCreatingJson: Boolean
        get() = System.getProperty("skyblockapi.generateRegexes.enabled")?.lowercase() == "true"

    var source: RegexSource = LOCAL_FILE
        private set

    private data class Node(
        var value: JsonElement? = null,
        val children: MutableMap<String, Node> = mutableMapOf()
    ) {
        fun toJson(): JsonElement {
            if (children.isEmpty()) return value ?: JsonObject()
            return JsonObjectBuilder {
                value?.let { set("@value", it) }

                for ((key, child) in children.toSortedMap()) {
                    var flattenedKey = key
                    var current = child

                    // if multiple nodes in a row only have a single children, we instead just make them
                    // all be in a single entry
                    while (current.value == null && current.children.size == 1) {
                        val (childKey, next) = current.children.entries.single()
                        flattenedKey += ".$childKey"
                        current = next
                    }

                    set(flattenedKey, current.toJson())
                }
            }
        }
    }

    context(regexes: Map<String, Regex>, regexLists: Map<String, List<Regex>>)
    internal fun createJson(): JsonElement {
        val map = mutableMapOf<String, JsonElement>()
        regexes.mapValuesTo(map) { (_, regex) -> JsonPrimitive(regex.pattern) }
        regexLists.mapValuesTo(map) { (_, list) -> JsonArrayBuilder { list.forEach { add(it.pattern) } } }

        val root = Node()
        for ((key, value) in map) {
            var node = root
            for (part in key.split('.')) {
                node = node.children.getOrPut(part, ::Node)
            }
            node.value = value
        }

        return root.toJson()
    }

    context(regexes: MutableMap<String, Regex>, regexLists: MutableMap<String, List<Regex>>)
    private fun loadRegexesFromJson(json: JsonObject) {
        fun regexOrNull(string: String) = runCatching { Regex(string) }.getOrNull()
        fun addWithPrefix(prefix: String = "", element: JsonElement) {
            when (element) {
                is JsonObject -> element.entrySet().forEach { (key, value) ->
                    if (key == "@value") addWithPrefix(prefix, value)
                    else addWithPrefix("$prefix.$key", value)
                }
                is JsonArray -> regexLists[prefix] = element.mapNotNull { it.asString() }.mapNotNull(::regexOrNull)
                else if element.isString -> regexes[prefix] = regexOrNull(element.asString) ?: return
                else -> SkyBlockAPI.error("Unexpected element prefix: '$prefix', element: $element")
            }
        }
        json.entrySet().forEach { (key, value) ->
            addWithPrefix(key, value)
        }
    }

    @JvmStatic
    @ApiStatus.Internal
    fun load() {
        if (McClient.isDev || isCreatingJson) return
        runCatchBlocking {
            val result = Http.getResult<JsonObject>(URL)

            val file = McClient.config.resolve(FILE_PATH)

            val json = result.onSuccess { json ->
                // on success, we write to local
                // TODO: check the date before writing. if the date is the same as the one in code,
                //  we don't need to write it
                source = REMOTE
                file.writeText(json.toPrettyString())
            }.recoverCatching {
                // We try to use the stored data if we can't get it from the server
                source = LOCAL_FILE
                // TODO: store data in the file that shows when it was actually updated,
                //  and check the date (?) of our current version
                if (!file.isRegularFile()) return@recoverCatching null
                file.readText().readJson<JsonObject>()
            }.getOrNull() ?: run {
                source = CODE
                return@runCatchBlocking
            }

            context(Regexes.regexes, Regexes.regexLists) {
                loadRegexesFromJson(json)
            }
        }
    }


    @Subscription
    fun onRegisterSkyblockApiCommands(event: RegisterSkyblockApiCommandsEvent) {
        event.register("dev regexes") {
            thenCallback("status") {
                Text.sendDebug {
                    append("Regexes loaded from: ")
                    append(this@RegexData.source.name, TextColor.AQUA)
                    append(".")
                }
                Text.sendDebug("Loaded ${Regexes.regexes.size.toFormattedString()} regexes and ${Regexes.regexLists.size.toFormattedString()} regex lists.")
            }
            thenCallback("copy") {
                context(Regexes.regexes, Regexes.regexLists) {
                    McClient.clipboard = createJson().toPrettyString()
                    Text.sendDebug("Copied regexes json to clipboard")
                }
            }

        }
    }


}
