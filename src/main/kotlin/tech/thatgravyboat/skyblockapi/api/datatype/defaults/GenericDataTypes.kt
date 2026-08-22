package tech.thatgravyboat.skyblockapi.api.datatype.defaults

import com.google.gson.JsonObject
import me.owdding.ktmodules.Module
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item
//? < 26.2
//import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import java.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Instant

@Module
object GenericDataTypes {

    val SKYBLOCK_ID: DataType<SkyBlockId> = DataType.of("skyblock_id") { SkyBlockId.createIdForItem(it) }
    val ID: DataType<String> = DataType.simple("id")
    val API_ID: DataType<String> = DataType.of("api_id") {
        when (val id = it.unsafeTag?.getStringOrNull("id")) {
            "RUNE", "UNIQUE_RUNE" -> USED_RUNE.resolve(it)?.id
            "PET" -> PET_DATA.resolve(it)?.apiId ?: return@of null
            else -> id
        }
    }
    val ID_DAMAGE: DataType<Int> = DataType.of("id_damage") {
        val id = ID.resolve(it) ?: return@of null
        val damage = id.substringAfterLast(":", "").toIntOrNull() ?: return@of null
        damage
    }
    val UUID: DataType<UUID> = DataType.simple("uuid")
    val MODIFIER: DataType<String> = DataType.simple("modifier")
    val TIMESTAMP: DataType<Instant> = DataType.of("timestamp") { it.unsafeTag?.getLongOrNull("timestamp")?.let(Instant::fromEpochMilliseconds) }
    val SECONDS_HELD: DataType<Int> = DataType.simple("seconds_held")
    val BOTTLE_OF_JYRRE_SECONDS: DataType<Int> = DataType.simple("bottle_of_jyrre_seconds")
    val RIFT_DISCRITE_SECONDS: DataType<Int> = DataType.simple("rift_discrite_seconds")

    val RECOMBOBULATOR: DataType<Boolean> = DataType.of("recombobulator") { item -> item.unsafeTag?.getIntOrNull("rarity_upgrades")?.let { it > 0 } }
    val QUIVER_ARROW: DataType<Boolean> = DataType.of("quiver_arrow") { it.unsafeTag?.getStringOrNull("quiver_arrow")?.equals("true") }
    val ENCHANTMENTS: DataType<Map<String, Int>> = DataType.of("enchantments") {
        it.unsafeTag?.getCompoundOrEmpty("enchantments")?.let { tag ->
            buildMap { tag.keySet().forEach { key -> this[key] = tag.getIntOr(key, 0) } }
        }
    }
    val HOT_POTATO_BOOKS: DataType<Int> = DataType.simple("hot_potato_count", "hot_potato_count")
    val ART_OF_WAR: DataType<Boolean> = DataType.simple("art_of_war", "art_of_war_count")
    val ART_OF_PEACE: DataType<Boolean> = DataType.simple("art_of_peace", "artOfPeaceApplied")
    val BOOK_OF_STATS: DataType<Int> = DataType.simple("book_of_stats", "stats_book")
    val RUNEBOOK: DataType<Int> = DataType.simple("runic_kills")
    val POTION_TYPE: DataType<String> = DataType.simple("potion_type")
    val POTION: DataType<String> = DataType.simple("potion")
    val POTION_LEVEL: DataType<Int> = DataType.simple("potion_level")
    val ATTRIBUTES: DataType<Map<String, Int>> = DataType.of("attributes") {
        it.unsafeTag?.getCompoundOrEmpty("attributes")?.let { tag ->
            buildMap { tag.keySet().forEach { key -> this[key] = tag.getIntOr(key, 0) } }
        }
    }
    val MIDAS_WEAPON_BID: DataType<Int> = DataType.simple("midas_weapon_bid", "winning_bid")
    val MIDAS_WEAPON_ADDED_COINS: DataType<Int> = DataType.simple("midas_weapon_added_coins", "additional_coins")
    val MIDAS_WEAPON_PAID: DataType<Long> = DataType.of("midas_weapon_paid") { stack ->
        listOfNotNull(MIDAS_WEAPON_BID.resolve(stack), MIDAS_WEAPON_ADDED_COINS.resolve(stack)).sum().toLong().takeUnless { it == 0L }
    }
    val ENRICHMENT: DataType<SkyBlockId> = DataType.of("enrichment") {
        val id = it.unsafeTag?.getStringOrNull("talisman_enrichment") ?: return@of null
        SkyBlockId.item("talisman_enrichment_$id")
    }
    val GILDED_GIFTED_COINS: DataType<Long> = DataType.simple("gilded_gifted_coins")
    val THUNDER_CHARGE: DataType<Int> = DataType.simple("thunder_charge")
    val PELTS_EARNED: DataType<Long> = DataType.simple("pelts_earned")
    val DONATED_MUSEUM: DataType<Boolean> = DataType.simple("donated_museum")
    val DAVID_CLOAK_UPGRADE: DataType<Int> = DataType.simple("attribute_menu_value", "attributeMenuValue")
    val ORIGIN_TAG: DataType<String> = DataType.simple("origin_tag", "originTag")

    val RAFFLE_WIN: DataType<String> = DataType.simple("raffle_win")
    val RAFFLE_YEAR: DataType<Int> = DataType.simple("raffle_year")

    val DITTO_USED: DataType<Boolean> = DataType.of("ditto_used") { item ->
        listOf("ditto_applied_skin", "ditto_og_item_id", "skinValue", "skullValue").any { it in (item.unsafeTag?.keySet() ?: emptySet()) }.takeIf { it }
    }
    val DITTO_ITEM_ID: DataType<String> = DataType.simple("ditto_og_item_id")

    val ABSORB_LOGS: DataType<Long> = DataType.simple("absorb_logs_chopped")
    val LOGS_CUT: DataType<Long> = DataType.simple("logs_cut")

    val STAR_COUNT: DataType<Int> = DataType.of("star_count") { it.unsafeTag?.getIntOrNull("upgrade_level") ?: it.unsafeTag?.getIntOrNull("dungeon_item_level") }
    val NECRON_SCROLLS: DataType<List<String>> = DataType.of("necron_scrolls") {
        val list = it.unsafeTag?.getList("ability_scroll")?.getOrNull()?.mapNotNull { list -> list.asString().getOrNull() }

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


    val PARTY_HAT_COLOR: DataType<String> = DataType.simple("party_hat_color")
    val PARTY_HAT_YEAR: DataType<Int> = DataType.simple("party_hat_year")

    val CULTIVATING_CROPS: DataType<Long> = DataType.simple("cultivating_crops", "farmed_cultivating")
    val TOOL_LEVEL: DataType<Int> = DataType.simple("tool_level", "levelable_lvl")
    val TOOL_EXP: DataType<Double> = DataType.simple("tool_exp", "levelable_exp")
    val TOOL_OVERCLOCKS: DataType<Int> = DataType.simple("tool_overclocks", "levelable_overclocks")

    //? < 26.2 {
    /*@RemoveNextVersion
    val APPLIED_RUNE: DataType<Pair<String, Int>> = DataType.of("applied_rune") {
        it.unsafeTag?.getCompoundOrEmpty("runes")?.let { tag ->
            buildMap { tag.keySet().forEach { key -> this[key] = tag.getIntOr(key, 0) } }
        }?.entries?.firstOrNull()?.toPair()
    }*///?}
    val USED_RUNE: DataType<SkyBlockId> = DataType.of("used_rune") {
        it.unsafeTag?.getCompoundOrEmpty("runes")?.let { tag ->
            tag.keySet().firstNotNullOfOrNull { key -> SkyBlockId.rune(key, tag.getIntOr(key, 0)) }
        }
    }
    val APPLIED_DYE: DataType<String> = DataType.simple("applied_dye", "dye_item")
    val HELMET_SKIN: DataType<String> = DataType.simple("helmet_skin", "skin")
    val PET_DATA: DataType<PetData> = DataType.of("pet_data") {
        val json = it.unsafeTag?.getStringOrNull("petInfo")?.readJson<JsonObject>() ?: return@of null
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
        it.unsafeTag?.getList("boosters")?.getOrNull()?.mapNotNull { list -> list.asString().getOrNull()?.let { "${it}_BOOSTER" } } ?: emptyList()
    }

    val WET_BOOK: DataType<Int> = DataType.simple("wet_book", "wet_book_count")
    val HOOK: DataType<Pair<UUID, String>> = getFishingRodPartDataType("hook")
    val LINE: DataType<Pair<UUID, String>> = getFishingRodPartDataType("line")
    val SINKER: DataType<Pair<UUID, String>> = getFishingRodPartDataType("sinker")

    /** In SkyBlock items that are only available in new versions are shown via [DataComponents.ITEM_MODEL], this returns the item that is displayed. */
    val VISIBLE_ITEM: DataType<Item> = DataType.of("visible_item") { it.get(DataComponents.ITEM_MODEL)?.let(BuiltInRegistries.ITEM::getOptional)?.getOrNull() }
    val CLEAN_NAME: DataType<String> = DataType.of("clean_name") { it.hoverName.stripped }

    val VINYLS: DataType<List<SkyBlockId>> = DataType.of("vinyls") {
        val map = it.unsafeTag?.getCompoundOrEmpty("vinyls")?.takeUnless { it.isEmpty } ?: return@of null
        map.values().mapNotNull { it.asString()?.map(SkyBlockId::item)?.getOrNull() }
    }

    val ETHERMERGE: DataType<Boolean> = DataType.simple("ethermerge")
    val TUNED_TRANSMISSION: DataType<Int> = DataType.simple("tuned_transmission")

    val SHINY: DataType<Boolean> = DataType.simple("is_shiny")

    val DOWSING_MODE: DataType<String> = DataType.simple("dowsing_mode")
    val HONEY_POT_USES: DataType<Int> = DataType.simple("honey_pot_uses")

    private fun getFishingRodPartDataType(name: String) = DataType.of(name) {
        val tag = it.unsafeTag?.getObjectOrNull(name) ?: return@of null
        val uuid = tag.getUuidOrNull("uuid") ?: UUID(0L, 0L)
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
