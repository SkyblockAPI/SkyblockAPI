package tech.thatgravyboat.skyblockapi.api.datatype.defaults

import com.google.gson.JsonObject
import me.owdding.ktmodules.Module
import net.minecraft.Util
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import java.util.UUID
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Instant

@Module
object GenericDataTypes {

    val SKYBLOCK_ID: DataType<SkyBlockId> = DataType.of("skyblock_id") { SkyBlockId.createIdForItem(it) }
    val ID: DataType<String> = DataType.simple("id")
    val API_ID: DataType<String> = DataType.of("api_id") {
        when (val id = it.tag?.getStringOrNull("id")) {
            "RUNE", "UNIQUE_RUNE" -> APPLIED_RUNE.factory(it)?.let { rune -> "rune:${rune.first}:${rune.second}" }
            "PET" -> PET_DATA.factory(it)?.apiId ?: return@of null
            else -> id
        }
    }
    val UUID: DataType<UUID> = DataType.simple("uuid")
    val MODIFIER: DataType<String> = DataType.simple("modifier")
    val TIMESTAMP: DataType<Instant> = DataType.of("timestamp") { it.tag?.getLongOrNull("timestamp")?.let(Instant::fromEpochMilliseconds) }
    val SECONDS_HELD: DataType<Int> = DataType.simple("seconds_held")
    val BOTTLE_OF_JYRRE_SECONDS: DataType<Int> = DataType.simple("bottle_of_jyrre_seconds")
    val RIFT_DISCRITE_SECONDS: DataType<Int> = DataType.simple("rift_discrite_seconds")

    val RECOMBOBULATOR: DataType<Boolean> = DataType.of("recombobulator") { item -> item.tag?.getIntOrNull("rarity_upgrades")?.let { it > 0 } }
    val QUIVER_ARROW: DataType<Boolean> = DataType.of("quiver_arrow") { it.tag?.getStringOrNull("quiver_arrow")?.equals("true") }
    val ENCHANTMENTS: DataType<Map<String, Int>> = DataType.of("enchantments") {
        it.tag?.getCompoundOrEmpty("enchantments")?.let { tag ->
            buildMap { tag.keySet().forEach { key -> this[key] = tag.getIntOr(key, 0) } }
        }
    }
    val HOT_POTATO_BOOKS: DataType<Int> = DataType.simple("hot_potato_count", "hot_potato_count")
    val ART_OF_WAR: DataType<Boolean> = DataType.simple("art_of_war", "art_of_war_count")
    val ART_OF_PEACE: DataType<Boolean> = DataType.simple("art_of_peace", "artOfPeaceApplied")
    val BOOK_OF_STATS: DataType<Int> = DataType.simple("book_of_stats", "stats_book")
    val POTION: DataType<String> = DataType.simple("potion")
    val POTION_LEVEL: DataType<Int> = DataType.simple("potion_level")
    val ATTRIBUTES: DataType<Map<String, Int>> = DataType.of("attributes") {
        it.tag?.getCompoundOrEmpty("attributes")?.let { tag ->
            buildMap { tag.keySet().forEach { key -> this[key] = tag.getIntOr(key, 0) } }
        }
    }
    val MIDAS_WEAPON_PAID: DataType<Long> = DataType.of("midas_weapon_paid") { stack ->
        listOf("winning_bid", "additional_coins").mapNotNull { stack.tag?.getLongOrNull(it) }.sum().takeUnless { it == 0L }
    }
    val ENRICHMENT: DataType<SkyBlockId> = DataType.of("enrichment") {
        val id = it.tag?.getStringOrNull("talisman_enrichment") ?: return@of null
        SkyBlockId.item("talisman_enrichment_$id")
    }
    val GILDED_GIFTED_COINS: DataType<Long> = DataType.simple("gilded_gifted_coins")
    val CROPS_BROKEN: DataType<Long> = DataType.simple("mined_crops")
    val ABSORB_LOGS: DataType<Long> = DataType.simple("absorb_logs_chopped")
    val LOGS_CUT: DataType<Long> = DataType.simple("logs_cut")
    val STAR_COUNT: DataType<Int> = DataType.of("star_count") { it.tag?.getIntOrNull("upgrade_level") ?: it.tag?.getIntOrNull("dungeon_item_level") }
    val NECRON_SCROLLS: DataType<List<String>> = DataType.of("necron_scrolls") {
        val list = it.tag?.getList("ability_scroll")?.getOrNull()?.mapNotNull { list -> list.asString().getOrNull() }

        return@of if (list?.contains("ULTIMATE_WITHER_SCROLL") == true) {
            listOf("WITHER_SHIELD_SCROLL", "SHADOW_WARP_SCROLL", "IMPLOSION_SCROLL")
        } else {
            list
        }
    }
    val DUNGEON_ITEM: DataType<Boolean> = DataType.simple("dungeon_item")
    val DUNGEON_TIER: DataType<Int> = DataType.simple("dungeon_tier", "item_tier")
    val DUNGEON_QUALITY: DataType<Int> = DataType.simple("dungeon_quality", "baseStatBoostPercentage")

    val ABICASE_MODEL: DataType<String> = DataType.simple("abicase_model", "model")
    val FUNGI_CUTTER_MODE: DataType<String> = DataType.simple("fungi_cutter_mode")


    val PARTY_HAT_COLOR: DataType<String> = DataType.simple("party_hat_color")
    val PARTY_HAT_YEAR: DataType<Int> = DataType.simple("party_hat_year")

    @RemoveNextVersion
    val APPLIED_RUNE: DataType<Pair<String, Int>> = DataType.of("applied_rune") {
        it.tag?.getCompoundOrEmpty("runes")?.let { tag ->
            buildMap { tag.keySet().forEach { key -> this[key] = tag.getIntOr(key, 0) } }
        }?.entries?.firstOrNull()?.toPair()
    }
    val USED_RUNE: DataType<SkyBlockId> = DataType("used_rune") {
        it.tag?.getCompoundOrEmpty("runes")?.let { tag ->
            tag.keySet().firstNotNullOfOrNull { key -> SkyBlockId.rune(key, tag.getIntOr(key, 0)) }
        }
    }
    val APPLIED_DYE: DataType<String> = DataType.simple("applied_dye", "dye_item")
    val HELMET_SKIN: DataType<String> = DataType.simple("helmet_skin", "skin")
    val PET_DATA: DataType<PetData> = DataType.of("pet_data") {
        val json = it.tag?.getStringOrNull("petInfo")?.readJson<JsonObject>() ?: return@of null
        PetData(
            json.get("type").asString(""),
            json.get("active").asBoolean(false),
            json.get("exp").asLong(0),
            SkyBlockRarity.fromName(json.get("tier").asString("")),
            json.get("heldItem")?.asString,
            json.get("skin")?.asString,
            json.get("candyUsed").asInt(0),
        )
    }
    val JALAPENO_BOOK: DataType<Boolean> = DataType.simple("jalapeno_book", "jalapeno_count")

    val BOOSTERS: DataType<List<String>> = DataType.of("boosters") {
        it.tag?.getList("boosters")?.getOrNull()?.mapNotNull { list -> list.asString().getOrNull()?.let { "${it}_BOOSTER" } } ?: emptyList()
    }

    val WET_BOOK: DataType<Int> = DataType.simple("wet_book", "wet_book_count")
    val HOOK: DataType<Pair<UUID, String>> = getFishingRodPartDataType("hook")
    val LINE: DataType<Pair<UUID, String>> = getFishingRodPartDataType("line")
    val SINKER: DataType<Pair<UUID, String>> = getFishingRodPartDataType("sinker")

    /** In SkyBlock items that are only available in new versions are shown via [DataComponents.ITEM_MODEL], this returns the item that is displayed. */
    val VISIBLE_ITEM: DataType<Item> = DataType.of("visible_item") { it.get(DataComponents.ITEM_MODEL)?.let(BuiltInRegistries.ITEM::getOptional)?.getOrNull() }


    private fun getFishingRodPartDataType(name: String) = DataType.of(name) {
        val tag = it.tag?.getObjectOrNull(name) ?: return@of null
        val uuid = tag.getUuidOrNull("uuid") ?: Util.NIL_UUID
        uuid to tag.getStringOr("part", "")
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
