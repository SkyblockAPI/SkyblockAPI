package tech.thatgravyboat.skyblockapi.impl.commands

import com.google.gson.JsonElement
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tech.thatgravyboat.skyblockapi.api.events.base.DevModule
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.impl.suggestion.IterableSuggestionProvider
import tech.thatgravyboat.skyblockapi.impl.suggestion.LayeredSuggestionProvider
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.imitateServerMessage
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.Text.sendWithPrefix
import kotlin.io.path.Path
import kotlin.io.path.PathWalkOption
import kotlin.io.path.extension
import kotlin.io.path.readText
import kotlin.io.path.walk

@DevModule
internal object MessageTestCommand : Logger by LoggerFactory.getLogger(MessageTestCommand::class.java) {

    private val raw: MutableMap<String, List<String>> = mutableMapOf()
    private val components: MutableMap<String, List<Component>> = mutableMapOf()

    init {
        val property = System.getProperty("tech.thatgravyboat.message_test_folder")
        if (property != null) {
            val folder = Path(property)
            folder.walk().forEach {
                runCatching {
                    val name = folder.relativize(it).toString()
                    if (it.extension == "json") {
                        val element = it.readText().readJson<JsonElement>()
                        if (element.isJsonArray) {
                            components[name.removeSuffix(".json")] = element.asJsonArray.map { it.toDataOrThrow(ComponentSerialization.CODEC) }
                        } else {
                            components[name.removeSuffix(".json")] = listOf(element.toDataOrThrow(ComponentSerialization.CODEC))
                        }
                    } else {
                        if (name.endsWith(".lines.raw")) {
                            raw[name.removeSuffix(".lines.raw")] = it.readText().takeUnless { it.isEmpty() }?.lines() ?: return@forEach warn("Failed to load raw lines $name")
                        } else {
                            raw[name.removeSuffix(".raw")] = it.readText().takeUnless { it.isEmpty() }?.let { listOf(it) } ?: return@forEach warn("Failed to load raw message $name")
                        }
                    }
                }.onFailure { err ->
                    error("Failed to load message file $it", err)
                }
            }
        }
    }

    @Subscription
    context(event: RegisterSkyblockApiCommandsEvent)
    private fun onCommand() {
        event.command("dev message_test") {
            "raw path"(StringArgumentType.greedyString(), IterableSuggestionProvider(raw.keys).withoutSanitization()) executes { path ->
                val message = raw[path]
                if (message == null) {
                    Text.of("Unable to find raw message $path").sendWithPrefix()
                    return@executes
                }
                message.forEach {
                    Text.of(it).imitateServerMessage()
                }
            }
            "component path"(StringArgumentType.greedyString(), IterableSuggestionProvider(components.keys).withoutSanitization()) executes { path ->
                val message = components[path]
                if (message == null) {
                    Text.of("Unable to find json message $path").sendWithPrefix()
                    return@executes
                }
                message.forEach {
                    it.imitateServerMessage()
                }
            }
        }
    }

}
