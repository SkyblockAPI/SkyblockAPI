package tech.thatgravyboat.skyblockapi.api.datatype.defaults

import com.google.gson.JsonObject
import kotlinx.datetime.Instant
import me.owdding.ktmodules.Module
import net.minecraft.Util
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.Item
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterDataTypesEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Module
object GenericDataTypes {

    val ID: DataType<String> = DataType("id") { it.tag?.getStringOrNull("id") }
    val API_ID: DataType<String> = DataType("api_id") {
        val id = it.tag?.getStringOrNull("id")
        when (id) {
            "RUNE", "UNIQUE_RUNE" -> {
                val rune = getAppliedRune(it.tag ?: return@DataType null)
                "rune:${rune?.first}:${rune?.second}"
            }
            "PET" -> getPetData(it.tag ?: return@DataType null)?.apiId ?: return@DataType null
            else -> id
        }
    }
    val UUID: DataType<UUID> = DataType("uuid") { it.tag?.getUuidOrNull("uuid") }
    val MODIFIER: DataType<String> = DataType("modifier") { it.tag?.getStringOrNull("modifier") }
    val TIMESTAMP: DataType<Instant> = DataType("timestamp") { it.tag?.getLongOrNull("timestamp")?.let(Instant::fromEpochMilliseconds) }
    val SECONDS_HELD: DataType<Int> = DataType("seconds_held") { it.tag?.getIntOrNull("seconds_held") }
    val BOTTLE_OF_JYRRE_SECONDS: DataType<Int> = DataType("bottle_of_jyrre_seconds") { it.tag?.getIntOrNull("bottle_of_jyrre_seconds") }
    val RIFT_DISCRITE_SECONDS: DataType<Int> = DataType("rift_discrite_seconds") { it.tag?.getIntOrNull("rift_discrite_seconds") }

    val PICKONIMBUS_DURABILITY: DataType<Int> = DataType("pickonimbus_durability") { it.tag?.getIntOrNull("pickonimbus_durability") }
    @RemoveNextVersion
    val RARITY_UPGRADES: DataType<Int> = DataType("rarity_upgrades") { it.tag?.getIntOrNull("rarity_upgrades") }
    val RECOMBOBULATOR: DataType<Boolean> = DataType("recombobulator") { item -> item.tag?.getIntOrNull("rarity_upgrades")?.let { it > 0 } }
    val QUIVER_ARROW: DataType<Boolean> = DataType("quiver_arrow") { it.tag?.getStringOrNull("quiver_arrow")?.equals("true") }
    val ENCHANTMENTS: DataType<Map<String, Int>> = DataType("enchantments") {
        it.tag?.getCompoundOrEmpty("enchantments")?.let { tag ->
            buildMap { tag.keySet().forEach { key -> this[key] = tag.getIntOr(key, 0) } }
        }
    }
    val HOT_POTATO_BOOKS: DataType<Int> = DataType("hot_potato_count") { it.tag?.getIntOrNull("hot_potato_count") }
    val ART_OF_WAR: DataType<Boolean> = DataType("art_of_war") { item -> item.tag?.getBooleanOrNull("art_of_war_count") }
    val ART_OF_PEACE: DataType<Boolean> = DataType("art_of_peace") { it.tag?.getBooleanOrNull("artOfPeaceApplied") }
    val BOOK_OF_STATS: DataType<Int> = DataType("book_of_stats") { it.tag?.getIntOrNull("stats_book") }
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
    val NECRON_SCROLLS: DataType<List<String>> = DataType("necron_scrolls") {
        val list = it.tag?.getList("ability_scroll")?.getOrNull()?.mapNotNull { list -> list.asString().getOrNull() }

        return@DataType if (list?.contains("ULTIMATE_WITHER_SCROLL") == true) {
            listOf("WITHER_SHIELD_SCROLL","SHADOW_WARP_SCROLL","IMPLOSION_SCROLL")
        } else {
            list
        }
    }
    val DUNGEON_ITEM: DataType<Boolean> = DataType("dungeon_item") { it.tag?.getBooleanOrNull("dungeon_item") }
    val DUNGEON_TIER: DataType<Int> = DataType("dungeon_tier") { it.tag?.getIntOrNull("item_tier") }
    val DUNGEON_QUALITY: DataType<Int> = DataType("dungeon_quality") { it.tag?.getIntOrNull("baseStatBoostPercentage") }

    val APPLIED_RUNE: DataType<Pair<String, Int>> = DataType("applied_rune") { getAppliedRune(it.tag ?: return@DataType null) }
    val APPLIED_DYE: DataType<String> = DataType("applied_dye") { it.tag?.getStringOrNull("dye_item") }
    val HELMET_SKIN: DataType<String> = DataType("helmet_skin") { it.tag?.getStringOrNull("skin") }
    val PET_DATA: DataType<PetData> = DataType("pet_data") { getPetData(it.tag ?: return@DataType null) }
    val DIVAN_POWDER_COATING: DataType<Int> = DataType("divan_powder_coating") { it.tag?.getIntOrNull("divan_powder_coating") }
    val POLARVOID: DataType<Int> = DataType("polarvoid") { it.tag?.getIntOrNull("polarvoid") }
    val POWER_ABILITY_SCROLL: DataType<String> = DataType("power_ability_scroll") { it.tag?.getStringOrNull("power_ability_scroll") }
    val JALAPENO_BOOK: DataType<Boolean> = DataType("jalapeno_book") { it.tag?.getBooleanOrNull("jalapeno_count") }

    val BOOSTERS: DataType<List<String>> = DataType("boosters") {
        it.tag?.getList("boosters")?.getOrNull()?.mapNotNull { list -> list.asString().getOrNull()?.let { "${it}_BOOSTER" } } ?: emptyList()
    }

    val HOOK: DataType<Pair<UUID, String>> = getFishingRodPartDataType("hook")
    val LINE: DataType<Pair<UUID, String>> = getFishingRodPartDataType("line")
    val SINKER: DataType<Pair<UUID, String>> = getFishingRodPartDataType("sinker")

    val FUEL_TANK: DataType<String> = DataType("drill_part_fuel_tank") { it.tag?.getStringOrNull("drill_part_fuel_tank") }
    val ENGINE: DataType<String> = DataType("drill_part_engine") { it.tag?.getStringOrNull("drill_part_engine") }
    val UPGRADE_MODULE: DataType<String> = DataType("drill_part_upgrade_module") { it.tag?.getStringOrNull("drill_part_upgrade_module") }

    /** In SkyBlock items that are only avaliable in new versions are showned via `DataComponents.ITEM_MODEL` this returns that item that is displayed. */
    val VISIBLE_ITEM: DataType<Item> = DataType("visible_item") { it.get(DataComponents.ITEM_MODEL)?.let(BuiltInRegistries.ITEM::getOptional)?.getOrNull() }

    @Subscription
    fun onDataTypeRegistration(event: RegisterDataTypesEvent) {
        event.register(ID)
        event.register(API_ID)
        event.register(UUID)
        event.register(MODIFIER)
        event.register(TIMESTAMP)
        event.register(SECONDS_HELD)
        event.register(BOTTLE_OF_JYRRE_SECONDS)
        event.register(RIFT_DISCRITE_SECONDS)
        event.register(PICKONIMBUS_DURABILITY)
        event.register(RARITY_UPGRADES)
        event.register(RECOMBOBULATOR)
        event.register(QUIVER_ARROW)
        event.register(ENCHANTMENTS)
        event.register(HOT_POTATO_BOOKS)
        event.register(ART_OF_WAR)
        event.register(ART_OF_PEACE)
        event.register(BOOK_OF_STATS)
        event.register(GEMSTONES)
        event.register(POTION)
        event.register(POTION_LEVEL)
        event.register(ATTRIBUTES)
        event.register(CROPS_BROKEN)
        event.register(COMPACT_BLOCKS)
        event.register(STAR_COUNT)
        event.register(NECRON_SCROLLS)
        event.register(DUNGEON_ITEM)
        event.register(DUNGEON_TIER)
        event.register(DUNGEON_QUALITY)
        event.register(APPLIED_RUNE)
        event.register(APPLIED_DYE)
        event.register(HELMET_SKIN)
        event.register(PET_DATA)
        event.register(DIVAN_POWDER_COATING)
        event.register(POLARVOID)
        event.register(POWER_ABILITY_SCROLL)
        event.register(JALAPENO_BOOK)
        event.register(HOOK)
        event.register(LINE)
        event.register(SINKER)
        event.register(FUEL_TANK)
        event.register(ENGINE)
        event.register(UPGRADE_MODULE)
        event.register(VISIBLE_ITEM)
        event.register(BOOSTERS)
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
            SkyBlockRarity.fromName(json.get("tier").asString("")),
            json.get("heldItem")?.asString,
            json.get("skin")?.asString,
            json.get("candyUsed").asInt(0),
        )
    }

    data class PetData(
        val id: String,
        val active: Boolean,
        val exp: Long,
        val rarity: SkyBlockRarity,
        val heldItem: String?,
        val skin: String?,
        val candyUsed: Int,
    ) {
        val apiId = "pet:$id:${rarity.name}"
    }
}
