package tech.thatgravyboat.skyblockapi.api.datatype.defaults

import com.google.gson.JsonObject
import me.owdding.ktmodules.Module
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.datatype.ResolutionContext
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.asBoolean
import tech.thatgravyboat.skyblockapi.utils.extentions.asInt
import tech.thatgravyboat.skyblockapi.utils.extentions.asLong
import tech.thatgravyboat.skyblockapi.utils.extentions.asString
import tech.thatgravyboat.skyblockapi.utils.extentions.getIntOrNull
import tech.thatgravyboat.skyblockapi.utils.extentions.getLongOrNull
import tech.thatgravyboat.skyblockapi.utils.extentions.getObjectOrNull
import tech.thatgravyboat.skyblockapi.utils.extentions.getStringOrNull
import tech.thatgravyboat.skyblockapi.utils.extentions.getUuidOrNull
import tech.thatgravyboat.skyblockapi.utils.extentions.unsafeTag
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import java.util.UUID
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull
import kotlin.time.Instant

//? < 26.2
//import tech.thatgravyboat.skyblockapi.RemoveNextVersion

@Module
public object GenericDataTypes {

    public val SKYBLOCK_ID: DataType<SkyBlockId> = DataType.of("skyblock_id") { ctx, item -> SkyBlockId.createIdForItem(item, ctx) }
    public val ID: DataType<String> = DataType.of("id") { ctx, _ -> ctx[ResolutionContext.Resolver.ID] }
    public val API_ID: DataType<String> = DataType.of("api_id") { ctx, item ->
        when (val id = item.unsafeTag?.getStringOrNull("id")) {
            "RUNE", "UNIQUE_RUNE" -> USED_RUNE.resolve(item, ctx)?.id
            "PET" -> ctx[ResolutionContext.Resolver.PET_DATA]?.apiId ?: return@of null
            else -> id
        }
    }
    public val ID_DAMAGE: DataType<Int> = DataType.of("id_damage") { ctx, item ->
        val id = ctx[ResolutionContext.Resolver.ID] ?: return@of null
        val damage = id.substringAfterLast(":", "").toIntOrNull() ?: return@of null
        damage
    }
    public val UUID: DataType<UUID> = DataType.simple("uuid")
    public val MODIFIER: DataType<String> = DataType.simple("modifier")
    public val TIMESTAMP: DataType<Instant> = DataType.of("timestamp") { it.unsafeTag?.getLongOrNull("timestamp")?.let(Instant::fromEpochMilliseconds) }
    public val SECONDS_HELD: DataType<Int> = DataType.simple("seconds_held")
    public val BOTTLE_OF_JYRRE_SECONDS: DataType<Int> = DataType.simple("bottle_of_jyrre_seconds")
    public val RIFT_DISCRITE_SECONDS: DataType<Int> = DataType.simple("rift_discrite_seconds")

    public val RECOMBOBULATOR: DataType<Boolean> = DataType.of("recombobulator") { item -> item.unsafeTag?.getIntOrNull("rarity_upgrades")?.let { it > 0 } }
    public val QUIVER_ARROW: DataType<Boolean> = DataType.of("quiver_arrow") { it.unsafeTag?.getStringOrNull("quiver_arrow")?.equals("true") }
    public val ENCHANTMENTS: DataType<Map<String, Int>> = DataType.of("enchantments") {
        it.unsafeTag?.getCompoundOrEmpty("enchantments")?.let { tag ->
            buildMap { tag.keySet().forEach { key -> this[key] = tag.getIntOr(key, 0) } }
        }
    }
    public val HOT_POTATO_BOOKS: DataType<Int> = DataType.simple("hot_potato_count", "hot_potato_count")
    public val ART_OF_WAR: DataType<Boolean> = DataType.simple("art_of_war", "art_of_war_count")
    public val ART_OF_PEACE: DataType<Boolean> = DataType.simple("art_of_peace", "artOfPeaceApplied")
    public val BOOK_OF_STATS: DataType<Int> = DataType.simple("book_of_stats", "stats_book")
    public val RUNEBOOK: DataType<Int> = DataType.simple("runic_kills")
    public val POTION_TYPE: DataType<String> = DataType.simple("potion_type")
    public val POTION: DataType<String> = DataType.simple("potion")
    public val POTION_LEVEL: DataType<Int> = DataType.simple("potion_level")
    public val ATTRIBUTES: DataType<Map<String, Int>> = DataType.of("attributes") {
        it.unsafeTag?.getCompoundOrEmpty("attributes")?.let { tag ->
            buildMap { tag.keySet().forEach { key -> this[key] = tag.getIntOr(key, 0) } }
        }
    }
    public val MIDAS_WEAPON_BID: DataType<Int> = DataType.simple("midas_weapon_bid", "winning_bid")
    public val MIDAS_WEAPON_ADDED_COINS: DataType<Int> = DataType.simple("midas_weapon_added_coins", "additional_coins")
    public val MIDAS_WEAPON_PAID: DataType<Long> = DataType.of("midas_weapon_paid") { ctx, stack ->
        listOfNotNull(MIDAS_WEAPON_BID.resolve(stack, ctx), MIDAS_WEAPON_ADDED_COINS.resolve(stack, ctx)).sum().toLong().takeUnless { it == 0L }
    }
    public val ENRICHMENT: DataType<SkyBlockId> = DataType.of("enrichment") {
        val id = it.unsafeTag?.getStringOrNull("talisman_enrichment") ?: return@of null
        SkyBlockId.item("talisman_enrichment_$id")
    }
    public val GILDED_GIFTED_COINS: DataType<Long> = DataType.simple("gilded_gifted_coins")
    public val THUNDER_CHARGE: DataType<Int> = DataType.simple("thunder_charge")
    public val PELTS_EARNED: DataType<Long> = DataType.simple("pelts_earned")
    public val DONATED_MUSEUM: DataType<Boolean> = DataType.simple("donated_museum")
    public val DAVID_CLOAK_UPGRADE: DataType<Int> = DataType.simple("attribute_menu_value", "attributeMenuValue")
    public val ORIGIN_TAG: DataType<String> = DataType.simple("origin_tag", "originTag")

    public val RAFFLE_WIN: DataType<String> = DataType.simple("raffle_win")
    public val RAFFLE_YEAR: DataType<Int> = DataType.simple("raffle_year")

    public val DITTO_USED: DataType<Boolean> = DataType.of("ditto_used") { item ->
        listOf("ditto_applied_skin", "ditto_og_item_id", "skinValue", "skullValue").any { it in (item.unsafeTag?.keySet() ?: emptySet()) }.takeIf { it }
    }
    public val DITTO_ITEM_ID: DataType<String> = DataType.simple("ditto_og_item_id")

    public val ABSORB_LOGS: DataType<Long> = DataType.simple("absorb_logs_chopped")
    public val LOGS_CUT: DataType<Long> = DataType.simple("logs_cut")

    public val STAR_COUNT: DataType<Int> = DataType.of("star_count") { it.unsafeTag?.getIntOrNull("upgrade_level") ?: it.unsafeTag?.getIntOrNull("dungeon_item_level") }
    public val NECRON_SCROLLS: DataType<List<String>> = DataType.of("necron_scrolls") {
        val list = it.unsafeTag?.getList("ability_scroll")?.getOrNull()?.mapNotNull { list -> list.asString().getOrNull() }

        return@of if (list?.contains("ULTIMATE_WITHER_SCROLL") == true) {
            listOf("WITHER_SHIELD_SCROLL", "SHADOW_WARP_SCROLL", "IMPLOSION_SCROLL")
        } else {
            list
        }
    }
    public val DUNGEON_ITEM: DataType<Boolean> = DataType.simple("dungeon_item")
    public val DUNGEON_TIER: DataType<Int> = DataType.simple("dungeon_tier", "item_tier")
    public val DUNGEON_QUALITY: DataType<Int> = DataType.simple("dungeon_quality", "baseStatBoostPercentage")

    public val ABICASE_MODEL: DataType<String> = DataType.simple("abicase_model", "model")


    public val PARTY_HAT_COLOR: DataType<String> = DataType.simple("party_hat_color")
    public val PARTY_HAT_YEAR: DataType<Int> = DataType.simple("party_hat_year")

    public val RABBIT_FACTION: DataType<String> = DataType.simple("faction_rabbit_id")

    public val CULTIVATING_CROPS: DataType<Long> = DataType.simple("cultivating_crops", "farmed_cultivating")
    public val TOOL_LEVEL: DataType<Int> = DataType.simple("tool_level", "levelable_lvl")
    public val TOOL_EXP: DataType<Double> = DataType.simple("tool_exp", "levelable_exp")
    public val TOOL_OVERCLOCKS: DataType<Int> = DataType.simple("tool_overclocks", "levelable_overclocks")

    //? < 26.2 {
    /*@RemoveNextVersion
    public val APPLIED_RUNE: DataType<Pair<String, Int>> = DataType.of("applied_rune") {
        it.unsafeTag?.getCompoundOrEmpty("runes")?.let { tag ->
            buildMap { tag.keySet().forEach { key -> this[key] = tag.getIntOr(key, 0) } }
        }?.entries?.firstOrNull()?.toPair()
    }*///?}
    public val USED_RUNE: DataType<SkyBlockId> = DataType.of("used_rune") {
        it.unsafeTag?.getCompoundOrEmpty("runes")?.let { tag ->
            tag.keySet().firstNotNullOfOrNull { key -> SkyBlockId.rune(key, tag.getIntOr(key, 0)) }
        }
    }
    public val APPLIED_DYE: DataType<String> = DataType.simple("applied_dye", "dye_item")
    public val HELMET_SKIN: DataType<String> = DataType.simple("helmet_skin", "skin")
    public val PET_DATA: DataType<PetData> = DataType.of("pet_data") { ctx, _ -> ctx[ResolutionContext.Resolver.PET_DATA] }
    public val JALAPENO_BOOK: DataType<Boolean> = DataType.simple("jalapeno_book", "jalapeno_count")

    //? < 26.3 {
    /*@Deprecated("Removed DataType", ReplaceWith("GenericDataTypes.BOOSTER_TIERS"))
    public val BOOSTERS: DataType<List<String>> = DataType.of("boosters") {
        it.unsafeTag?.getList("boosters")?.getOrNull()?.mapNotNull { list -> list.asString().getOrNull()?.let { "${it}_BOOSTER" } } ?: emptyList()
    }*///?}

    public val BOOSTER_TIERS: DataType<Map<String, Int>> = DataType.of("booster_tiers") {
        it.unsafeTag?.getCompound("booster_tiers")?.getOrNull()?.entrySet()?.associate { (k, v) ->
            k to v.asInt().getOrElse { 0 }
        } ?: emptyMap()
    }

    public val WET_BOOK: DataType<Int> = DataType.simple("wet_book", "wet_book_count")
    public val HOOK: DataType<Pair<UUID, String>> = getFishingRodPartDataType("hook")
    public val LINE: DataType<Pair<UUID, String>> = getFishingRodPartDataType("line")
    public val SINKER: DataType<Pair<UUID, String>> = getFishingRodPartDataType("sinker")

    /** In SkyBlock items that are only available in new versions are shown via [DataComponents.ITEM_MODEL], this returns the item that is displayed. */
    public val VISIBLE_ITEM: DataType<Item> = DataType.of("visible_item") { it.get(DataComponents.ITEM_MODEL)?.let(BuiltInRegistries.ITEM::getOptional)?.getOrNull() }
    public val CLEAN_NAME: DataType<String> = DataType.of("clean_name") { it.hoverName.stripped }

    public val VINYLS: DataType<List<SkyBlockId>> = DataType.of("vinyls") {
        val map = it.unsafeTag?.getCompoundOrEmpty("vinyls")?.takeUnless { it.isEmpty } ?: return@of null
        map.values().mapNotNull { it.asString()?.map(SkyBlockId::item)?.getOrNull() }
    }

    public val ETHERMERGE: DataType<Boolean> = DataType.simple("ethermerge")
    public val TUNED_TRANSMISSION: DataType<Int> = DataType.simple("tuned_transmission")

    public val SHINY: DataType<Boolean> = DataType.simple("is_shiny")

    public val DOWSING_MODE: DataType<String> = DataType.simple("dowsing_mode")
    public val HONEY_POT_USES: DataType<Int> = DataType.simple("honey_pot_uses")

    private fun getFishingRodPartDataType(name: String) = DataType.of(name) {
        val tag = it.unsafeTag?.getObjectOrNull(name) ?: return@of null
        val uuid = tag.getUuidOrNull("uuid") ?: UUID(0L, 0L)
        uuid to tag.getStringOr("part", "")
    }

    public data class PetData(
        val id: String,
        val active: Boolean,
        val exp: Long,
        val rarity: SkyBlockRarity,
        val heldItem: String?,
        val skin: String?,
        val candyUsed: Int,
    ) {
        val apiId: String = "pet:$id:${rarity.name}"

        internal companion object {
            fun fromItem(stack: ItemStack): PetData? {
                val json = stack.unsafeTag?.getStringOrNull("petInfo")?.readJson<JsonObject>() ?: return null
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
        }
    }
}
