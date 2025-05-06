package tech.thatgravyboat.skyblockapi.api.datatype.defaults

import com.google.gson.JsonObject
import kotlinx.datetime.Instant
import net.minecraft.Util
import net.minecraft.nbt.CompoundTag
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterDataTypesEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.extentions.tag
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Module
object GenericDataTypes {

    val ID: DataType<String> = DataType("id") { it.tag?.getStringOrNull("id") }
    val API_ID: DataType<String> = DataType("api_id") {
        val id = it.tag?.getStringOrNull("id")
        if (id == "RUNE") {
            val rune = getAppliedRune(it.tag ?: return@DataType null)
            "rune:${rune?.first}:${rune?.second}"
        } else if (id == "PET") {
            val pet = getPetData(it.tag ?: return@DataType null)
            "pet:${pet?.id}:${pet?.rarity?.name}"
        } else id
    }
    val UUID: DataType<UUID> = DataType("uuid") { it.tag?.getUuidOrNull("uuid") }
    val MODIFIER: DataType<String> = DataType("modifier") { it.tag?.getStringOrNull("modifier") }
    val TIMESTAMP: DataType<Instant> = DataType("timestamp") { it.tag?.getLongOrNull("timestamp")?.let(Instant::fromEpochMilliseconds) }
    val SECONDS_HELD: DataType<Int> = DataType("seconds_held") { it.tag?.getIntOrNull("seconds_held") }
    val PICKONIMBUS_DURABILITY: DataType<Int> = DataType("pickonimbus_durability") { it.tag?.getIntOrNull("pickonimbus_durability") }
    val RARITY_UPGRADES: DataType<Int> = DataType("rarity_upgrades") { it.tag?.getIntOrNull("rarity_upgrades") }
    val QUIVER_ARROW: DataType<Boolean> = DataType("quiver_arrow") { it.tag?.getStringOrNull("quiver_arrow")?.equals("true") }
    val ENCHANTMENTS: DataType<Map<String, Int>> = DataType("enchantments") {
        it.tag?.getCompoundOrEmpty("enchantments")?.let { tag ->
            buildMap { tag.keySet().forEach { key -> this[key] = tag.getIntOr(key, 0) } }
        }
    }
    val HOT_POTATO_BOOKS: DataType<Int> = DataType("hot_potato_count") { it.tag?.getIntOrNull("hot_potato_count") }
    val GEMSTONES: DataType<List<GemstoneSlotData>> = DataType("gemstones") { it.tag?.let(::parseGemstones) }
    val POTION: DataType<String> = DataType("potion") { it.tag?.getStringOrNull("potion") }
    val POTION_LEVEL: DataType<Int> = DataType("potion_level") { it.tag?.getIntOrNull("potion_level") }
    val ATTRIBUTES: DataType<Map<String, Int>> = DataType("attributes") {
        it.tag?.getCompoundOrEmpty("attributes")?.let { tag ->
            buildMap { tag.keySet().forEach { key -> this[key] = tag.getIntOr(key, 0) } }
        }
    }
    val CROPS_BROKEN: DataType<Long> = DataType("mined_crops") { it.tag?.getLongOrNull("mined_crops") }
    val COMPACT_BLOCKS: DataType<Long> = DataType("compact_blocks") { it.tag?.getLongOrNull("compact_blocks") }
    val STAR_COUNT: DataType<Int> = DataType("star_count") { it.tag?.getIntOrNull("upgrade_level") ?: it.tag?.getIntOrNull("dungeon_item_level") }
    val DUNGEON_ITEM: DataType<Boolean> = DataType("dungeon_item") { it.tag?.getBoolean("dungeon_item")?.getOrNull() }
    val APPLIED_RUNE: DataType<Pair<String, Int>> = DataType("applied_rune") { getAppliedRune(it.tag ?: return@DataType null) }
    val PET_DATA: DataType<PetData> = DataType("pet_data") { getPetData(it.tag ?: return@DataType null) }

    val HOOK: DataType<Pair<UUID, String>> = getFishingRodPartDataType("hook")
    val LINE: DataType<Pair<UUID, String>> = getFishingRodPartDataType("line")
    val SINKER: DataType<Pair<UUID, String>> = getFishingRodPartDataType("sinker")

    val FUEL_TANK: DataType<String> = DataType("drill_part_fuel_tank") { it.tag?.getStringOrNull("drill_part_fuel_tank") }
    val ENGINE: DataType<String> = DataType("drill_part_engine") { it.tag?.getStringOrNull("drill_part_engine") }
    val UPGRADE_MODULE: DataType<String> = DataType("drill_part_upgrade_module") { it.tag?.getStringOrNull("drill_part_upgrade_module") }

    @Subscription
    fun onDataTypeRegistration(event: RegisterDataTypesEvent) {
        event.register(ID)
        event.register(API_ID)
        event.register(UUID)
        event.register(MODIFIER)
        event.register(TIMESTAMP)
        event.register(SECONDS_HELD)
        event.register(PICKONIMBUS_DURABILITY)
        event.register(RARITY_UPGRADES)
        event.register(QUIVER_ARROW)
        event.register(ENCHANTMENTS)
        event.register(HOT_POTATO_BOOKS)
        event.register(GEMSTONES)
        event.register(POTION)
        event.register(POTION_LEVEL)
        event.register(ATTRIBUTES)
        event.register(CROPS_BROKEN)
        event.register(COMPACT_BLOCKS)
        event.register(STAR_COUNT)
        event.register(DUNGEON_ITEM)
        event.register(APPLIED_RUNE)
        event.register(PET_DATA)
        event.register(HOOK)
        event.register(LINE)
        event.register(SINKER)
        event.register(FUEL_TANK)
        event.register(ENGINE)
        event.register(UPGRADE_MODULE)
    }

    private fun getFishingRodPartDataType(name: String) = DataType(name) {
        val tag = it.tag?.getObjectOrNull(name) ?: return@DataType null
        val uuid = tag.getUuidOrNull("uuid") ?: Util.NIL_UUID
        uuid to tag.getStringOr("part", "")
    }

    private fun getAppliedRune(tag: CompoundTag): Pair<String, Int>? {
        return tag.getCompoundOrEmpty("runes")?.let { tag ->
            buildMap { tag.keySet().forEach { key -> this[key] = tag.getIntOr(key, 0) } }
        }?.entries?.firstOrNull()?.toPair()
    }

    private fun getPetData(tag: CompoundTag): PetData? {
        val json = tag.getStringOrNull("petInfo")?.readJson<JsonObject>() ?: return null
        return PetData(
            json.get("type").asString(""),
            json.get("active").asBoolean(false),
            json.get("exp").asLong(0),
            SkyBlockRarity.entries.find { rarity -> rarity.name.equals(json.get("tier").asString(), true) } ?: SkyBlockRarity.COMMON,
            json.get("heldItem")?.asString,
            json.get("candyUsed").asInt(0),
        )
    }

    data class PetData(
        val id: String,
        val active: Boolean,
        val exp: Long,
        val rarity: SkyBlockRarity,
        val heldItem: String?,
        val candyUsed: Int,
    )
}
