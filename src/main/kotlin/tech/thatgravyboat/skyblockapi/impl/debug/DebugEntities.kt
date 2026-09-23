package tech.thatgravyboat.skyblockapi.impl.debug

import com.google.gson.JsonElement
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.serialization.Codec
import me.owdding.ktmodules.Module
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.LiteralCommandBuilder
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.generated.CodecUtils
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.helpers.getAttachedLines
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import tech.thatgravyboat.skyblockapi.platform.save
import tech.thatgravyboat.skyblockapi.platform.skin
import tech.thatgravyboat.skyblockapi.platform.textureUrl
import tech.thatgravyboat.skyblockapi.utils.codecs.IncludedCodecs
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.json.JsonArray
import tech.thatgravyboat.skyblockapi.utils.json.JsonObject
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import kotlin.jvm.optionals.getOrNull

@Module
object DebugEntities {

    private val suggestions = SuggestionProvider<FabricClientCommandSource> { _, builder ->
        builder.suggest("*") // Suggest all entities
        SharedSuggestionProvider.suggestResource(BuiltInRegistries.ENTITY_TYPE.keySet(), builder)
    }

    private fun getEntityFilter(input: String): ((EntityType<*>) -> Boolean) {
        if (input == "*") {
            return { true } // Match all entity types
        }
        val id = Identifiers.parse(input) ?: return { false }
        val type = BuiltInRegistries.ENTITY_TYPE.getOptional(id).getOrNull() ?: return { false }
        return { it == type }
    }

    private fun copyEntitiesToClipboard(query: String, range: Int? = null, includeAttachments: Boolean = false) {
        val filter = getEntityFilter(query)
        val level = McLevel.selfOrNull ?: return
        val entities = if (range != null) {
            level.getEntities(null, McPlayer.self!!.boundingBox.inflate(range.toDouble()))
        } else {
            level.entitiesForRendering()
        }
        val filteredEntities = entities.filter { entity -> filter(entity.type) }

        val savedEntities = filteredEntities.mapNotNull { entity ->
            runCatching { entity.save() }.getOrNull()
        }

        val json = JsonArray {
            savedEntities.forEach { tag ->
                tag.toJson(CompoundTag.CODEC)?.let(this::add)
            }
        }

        if (json.isEmpty) {
            Text.sendDebug("No entities matched the filter: $query")
            return
        }

        var data: JsonElement = json

        if (includeAttachments) run {
            @Suppress("UNCHECKED_CAST")
            val codec = CodecUtils.map(Codec.STRING, CodecUtils.list(IncludedCodecs.COMPONENT)) as Codec<Map<String, List<Component>>>

            val attachments = filteredEntities.mapNotNull { entity ->
                val lines = entity.getAttachedLines()
                if (lines.isEmpty()) return@mapNotNull null
                // we convert entity id to string because json cannot have ints as keys
                entity.id.toString() to lines
            }.toMap().toJson(codec)

            if (attachments == null) {
                Text.sendDebug("Failed to copy entity attachments!")
                return@run
            }

            data = JsonObject {
                set("entities", json)
                set("attachments", attachments)
            }
        }

        if (json.size() != savedEntities.size) {
            Text.debug("Failed to serialize some entities, some may not be copied.").send()
        }

        Text.sendDebug("Copied ${json.size()} entities ") {
            if (includeAttachments) append("and attachments ")
            append("to clipboard with filter: $filter")
        }
        McClient.clipboard = data.toPrettyString()
    }

    private fun getHoveredEntity(): Entity? {
        val hoveredEntity = McClient.self.crosshairPickEntity
        if (hoveredEntity == null) {
            Text.debug("No entity is currently hovered.").send()
        }
        return hoveredEntity
    }



    @Subscription
    internal fun onCommandsRegistration(event: RegisterSkyblockApiCommandsEvent) {
        context(builder: LiteralCommandBuilder)
        fun copyEntitiesCommand(includeAttachments: Boolean) {
            builder.then("range", IntegerArgumentType.integer()) {
                then("filter", StringArgumentType.greedyString(), suggestions) {
                    callback {
                        copyEntitiesToClipboard(argument("filter"), argument("range"), includeAttachments)
                    }
                }
            }
        }
        event.register("copy") {
            then("entities") { copyEntitiesCommand(false) }
            then("entities_with_attachments") { copyEntitiesCommand(true) }
            then("entity") {
                then("texture") {
                    callback {
                        getHoveredEntity()?.let {
                            if (it is AbstractClientPlayer) {
                                it.skin().textureUrl?.let { McClient.clipboard = it }
                                Text.debug("Copied texture to clipboard.").send()
                            } else {
                                Text.debug("Hovered entity is not a player, cannot copy texture.").send()
                            }
                        }
                    }
                }

                callback {
                    val hoveredEntity = McClient.self.crosshairPickEntity
                    if (hoveredEntity == null) {
                        Text.debug("No entity is currently hovered.").send()
                    } else {
                        val json = hoveredEntity.save().toJson(CompoundTag.CODEC).toPrettyString()
                        McClient.clipboard = json
                        Text.debug("Copied entity ${hoveredEntity.name} to clipboard: $json").send()
                    }
                }
            }
        }
    }
}
