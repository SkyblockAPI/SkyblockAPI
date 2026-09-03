package tech.thatgravyboat.skyblockapi.api.datatype

import net.minecraft.world.item.Item
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockCategory
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.*
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.GenericDataTypes.PetData
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import java.util.*
import kotlin.time.Duration
import kotlin.time.Instant

object DataTypes {

    // General
    val SKYBLOCK_ID: DataType<SkyBlockId> = GenericDataTypes.SKYBLOCK_ID
    val ID: DataType<String> = GenericDataTypes.ID
    val API_ID: DataType<String> = GenericDataTypes.API_ID
    val ID_DAMAGE: DataType<Int> = GenericDataTypes.ID_DAMAGE
    val UUID: DataType<UUID> = GenericDataTypes.UUID
    val TIMESTAMP: DataType<Instant> = GenericDataTypes.TIMESTAMP
    val RARITY: DataType<SkyBlockRarity> = LoreDataTypes.RARITY
    val CATEGORY: DataType<SkyBlockCategory> = LoreDataTypes.CATEGORY
    val VISIBLE_ITEM: DataType<Item> = GenericDataTypes.VISIBLE_ITEM
    val CLEAN_NAME: DataType<String> = GenericDataTypes.CLEAN_NAME
    val ORIGIN_TAG: DataType<String> = GenericDataTypes.ORIGIN_TAG

    // Item Modifiers
    val MODIFIER: DataType<String> = GenericDataTypes.MODIFIER
    val RECOMBOBULATOR: DataType<Boolean> = GenericDataTypes.RECOMBOBULATOR
    val ENCHANTMENTS: DataType<Map<String, Int>> = GenericDataTypes.ENCHANTMENTS
    val ATTRIBUTES: DataType<Map<String, Int>> = GenericDataTypes.ATTRIBUTES
    val HOT_POTATO_BOOKS: DataType<Int> = GenericDataTypes.HOT_POTATO_BOOKS
    val ART_OF_WAR: DataType<Boolean> = GenericDataTypes.ART_OF_WAR
    val ART_OF_PEACE: DataType<Boolean> = GenericDataTypes.ART_OF_PEACE
    @Deprecated("Removed DataType", ReplaceWith("GenericDataTypes.BOOSTER_TIERS"))
    val BOOSTERS: DataType<List<String>> = GenericDataTypes.BOOSTERS
    val BOOSTER_TIERS: DataType<Map<SkyBlockId, Int>> = GenericDataTypes.BOOSTER_TIERS
    val JALAPENO_BOOK: DataType<Boolean> = GenericDataTypes.JALAPENO_BOOK
    val MIDAS_WEAPON_BID: DataType<Int> = GenericDataTypes.MIDAS_WEAPON_BID
    val MIDAS_WEAPON_ADDED_COINS: DataType<Int> = GenericDataTypes.MIDAS_WEAPON_ADDED_COINS
    val MIDAS_WEAPON_PAID: DataType<Long> = GenericDataTypes.MIDAS_WEAPON_PAID
    val ENRICHMENT: DataType<SkyBlockId> = GenericDataTypes.ENRICHMENT
    val SOULBOUND: DataType<LoreDataTypes.SoulboundType> = LoreDataTypes.SOULBOUND
    val ETHERMERGE: DataType<Boolean> = GenericDataTypes.ETHERMERGE
    val TUNED_TRANSMISSION: DataType<Int> = GenericDataTypes.TUNED_TRANSMISSION


    // Misc (idfk)
    val RIGHT_CLICK_MANA_ABILITY: DataType<Pair<String, Int>> = LoreDataTypes.RIGHT_CLICK_MANA_ABILITY
    val COOLDOWN_ABILITY: DataType<Pair<String, Duration>> = LoreDataTypes.COOLDOWN_ABILITY
    val QUIVER_ARROW: DataType<Boolean> = GenericDataTypes.QUIVER_ARROW
    val SELECTED_ARROW: DataType<SkyBlockId> = LoreDataTypes.SELECTED_ARROW
    val PERSONAL_COMPACTOR_ITEMS: DataType<List<String?>> = PersonalAccessoryDataTypes.PERSONAL_COMPACTOR_ITEMS
    val PERSONAL_DELETOR_ITEMS: DataType<List<String?>> = PersonalAccessoryDataTypes.PERSONAL_DELETOR_ITEMS
    val PERSONAL_ACCESSORY_ACTIVE: DataType<Boolean> = PersonalAccessoryDataTypes.PERSONAL_ACCESSORY_ACTIVE
    val POTION: DataType<String> = GenericDataTypes.POTION
    val POTION_TYPE: DataType<String> = GenericDataTypes.POTION_TYPE
    val POTION_LEVEL: DataType<Int> = GenericDataTypes.POTION_LEVEL
    val BOOK_OF_STATS: DataType<Int> = GenericDataTypes.BOOK_OF_STATS
    val RUNEBOOK: DataType<Int> = GenericDataTypes.RUNEBOOK
    val DOWSING_MODE: DataType<String> = GenericDataTypes.DOWSING_MODE
    val HONEY_POT_USES: DataType<Int> = GenericDataTypes.HONEY_POT_USES

    val USED_RUNE: DataType<SkyBlockId> = GenericDataTypes.USED_RUNE
    val HELMET_SKIN: DataType<String> = GenericDataTypes.HELMET_SKIN
    val APPLIED_DYE: DataType<String> = GenericDataTypes.APPLIED_DYE
    val SHINY: DataType<Boolean> = GenericDataTypes.SHINY
    val PET_DATA: DataType<PetData> = GenericDataTypes.PET_DATA
    val SNOWBALLS: DataType<Pair<Int, Int>> = LoreDataTypes.SNOWBALLS
    val ABSORB_LOGS: DataType<Long> = GenericDataTypes.ABSORB_LOGS
    val LOGS_CUT: DataType<Long> = GenericDataTypes.LOGS_CUT
    val GILDED_GIFTED_COINS: DataType<Long> = GenericDataTypes.GILDED_GIFTED_COINS
    val ABICASE_MODEL: DataType<String> = GenericDataTypes.ABICASE_MODEL
    val THUNDER_CHARGE: DataType<Int> = GenericDataTypes.THUNDER_CHARGE
    val PELTS_EARNED: DataType<Long> = GenericDataTypes.PELTS_EARNED
    val DONATED_MUSEUM: DataType<Boolean> = GenericDataTypes.DONATED_MUSEUM
    val DAVID_CLOAK_UPGRADE: DataType<Int> = GenericDataTypes.DAVID_CLOAK_UPGRADE

    val RAFFLE_WIN: DataType<String> = GenericDataTypes.RAFFLE_WIN
    val RAFFLE_YEAR: DataType<Int> = GenericDataTypes.RAFFLE_YEAR

    val DITTO_USED: DataType<Boolean> = GenericDataTypes.DITTO_USED
    val DITTO_ITEM_ID: DataType<String> = GenericDataTypes.DITTO_ITEM_ID

    val PARTY_HAT_COLOR: DataType<String> = GenericDataTypes.PARTY_HAT_COLOR
    val PARTY_HAT_YEAR: DataType<Int> = GenericDataTypes.PARTY_HAT_YEAR

    val RABBIT_FACTION: DataType<String> = GenericDataTypes.RABBIT_FACTION

    // Aging Items
    val SECONDS_HELD: DataType<Int> = GenericDataTypes.SECONDS_HELD
    val BOTTLE_OF_JYRRE_SECONDS: DataType<Int> = GenericDataTypes.BOTTLE_OF_JYRRE_SECONDS
    val RIFT_DISCRITE_SECONDS: DataType<Int> = GenericDataTypes.RIFT_DISCRITE_SECONDS

    // Dungeons
    val DUNGEON_ITEM: DataType<Boolean> = GenericDataTypes.DUNGEON_ITEM
    val STAR_COUNT: DataType<Int> = GenericDataTypes.STAR_COUNT
    val NECRON_SCROLLS: DataType<List<String>> = GenericDataTypes.NECRON_SCROLLS
    val DUNGEON_TIER: DataType<Int> = GenericDataTypes.DUNGEON_TIER
    val DUNGEON_QUALITY: DataType<Int> = GenericDataTypes.DUNGEON_QUALITY
    val DUNGEONBREAKER_CHARGES: DataType<Pair<Int, Int>> = LoreDataTypes.DUNGEONBREAKER_CHARGES

    // Fishing Rod
    val WET_BOOK: DataType<Int> = GenericDataTypes.WET_BOOK
    val HOOK: DataType<Pair<UUID, String>> = GenericDataTypes.HOOK
    val LINE: DataType<Pair<UUID, String>> = GenericDataTypes.LINE
    val SINKER: DataType<Pair<UUID, String>> = GenericDataTypes.SINKER

    // Mining
    val FUEL: DataType<Pair<Int, Int>> = LoreDataTypes.FUEL
    val PICKONIMBUS_DURABILITY: DataType<Int> = MiningDataTypes.PICKONIMBUS_DURABILITY
    val COMPACT_BLOCKS: DataType<Long> = MiningDataTypes.COMPACT_BLOCKS
    val GEMSTONES: DataType<List<GemstoneSlotData>> = MiningDataTypes.GEMSTONES
    val DIVAN_POWDER_COATING: DataType<Int> = MiningDataTypes.DIVAN_POWDER_COATING
    val POLARVOID: DataType<Int> = MiningDataTypes.POLARVOID
    val POWER_ABILITY_SCROLL: DataType<String> = MiningDataTypes.POWER_ABILITY_SCROLL
    val FUEL_TANK: DataType<String> = MiningDataTypes.FUEL_TANK
    val ENGINE: DataType<String> = MiningDataTypes.ENGINE
    val UPGRADE_MODULE: DataType<String> = MiningDataTypes.UPGRADE_MODULE

    // Farming
    val CULTIVATING_CROPS: DataType<Long> = GenericDataTypes.CULTIVATING_CROPS
    val TOOL_LEVEL: DataType<Int> = GenericDataTypes.TOOL_LEVEL
    val TOOL_EXP: DataType<Double> = GenericDataTypes.TOOL_EXP
    val TOOL_OVERCLOCKS: DataType<Int> = GenericDataTypes.TOOL_OVERCLOCKS
    val WATER_LEVEL: DataType<Pair<Int, Int>> = LoreDataTypes.WATER_LEVEL
    val VINYLS: DataType<List<SkyBlockId>> = GenericDataTypes.VINYLS
}
