package tech.thatgravyboat.skyblockapi.impl.debug

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.suggestion.SuggestionProvider
import me.owdding.ktmodules.Module
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.extensions.save
import tech.thatgravyboat.skyblockapi.utils.extensions.saveWithoutId
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.json.JsonArray
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
        val id = ResourceLocation.tryParse(input) ?: return { false }
        val type = BuiltInRegistries.ENTITY_TYPE.getOptional(id).getOrNull() ?: return { false }
        return { it == type }
    }

    private fun copyEntitiesToClipboard(query: String, range: Int? = null) {
        val filter = getEntityFilter(query)
        val level = McLevel.self as? ClientLevel ?: return
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

        if (json.size() != savedEntities.size) {
            Text.debug("Failed to serialize some entities, some may not be copied.").send()
        } else if (json.isEmpty) {
            Text.debug("No entities matched the filter: $query").send()
        }

        if (!json.isEmpty) {
            Text.debug("Copied ${json.size()} entities to clipboard with filter: $filter").send()
            McClient.clipboard = json.toPrettyString()
        }
    }

    private fun getHoveredEntity(): Entity? {
        val hoveredEntity = McClient.self.crosshairPickEntity
        if (hoveredEntity == null) {
            Text.debug("No entity is currently hovered.").send()
        }
        return hoveredEntity
    }

    @Subscription
    fun onCommandsRegistration(event: RegisterCommandsEvent) {
        event.register("sbapi copy entities") {
            then("range", IntegerArgumentType.integer()) {
                then("filter", StringArgumentType.greedyString(), suggestions) {
                    callback {
                        val range = IntegerArgumentType.getInteger(this, "range")
                        val filter = StringArgumentType.getString(this, "filter")
                        copyEntitiesToClipboard(filter, range)
                    }
                }
            }
        }

        event.register("sbapi copy entity") {
            then("texture") {
                callback {
                    getHoveredEntity()?.let {
                        if (it is AbstractClientPlayer) {
                            it.skin.textureUrl()?.let { McClient.clipboard = it }
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
