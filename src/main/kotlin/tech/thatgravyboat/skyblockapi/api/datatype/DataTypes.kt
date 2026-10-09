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

public object DataTypes {

    // General
    public val SKYBLOCK_ID: DataType<SkyBlockId> = GenericDataTypes.SKYBLOCK_ID
    public val ID: DataType<String> = GenericDataTypes.ID
    public val API_ID: DataType<String> = GenericDataTypes.API_ID
    public val ID_DAMAGE: DataType<Int> = GenericDataTypes.ID_DAMAGE
    public val UUID: DataType<UUID> = GenericDataTypes.UUID
    public val TIMESTAMP: DataType<Instant> = GenericDataTypes.TIMESTAMP
    public val RARITY: DataType<SkyBlockRarity> = LoreDataTypes.RARITY
    public val CATEGORY: DataType<SkyBlockCategory> = LoreDataTypes.CATEGORY
    public val VISIBLE_ITEM: DataType<Item> = GenericDataTypes.VISIBLE_ITEM
    public val CLEAN_NAME: DataType<String> = GenericDataTypes.CLEAN_NAME
    public val RAW_LORE: DataType<List<String>> = LoreDataTypes.RAW_LORE
    public val ORIGIN_TAG: DataType<String> = GenericDataTypes.ORIGIN_TAG

    // Item Modifiers
    public val MODIFIER: DataType<String> = GenericDataTypes.MODIFIER
    public val RECOMBOBULATOR: DataType<Boolean> = GenericDataTypes.RECOMBOBULATOR
    public val ENCHANTMENTS: DataType<Map<String, Int>> = GenericDataTypes.ENCHANTMENTS
    public val ATTRIBUTES: DataType<Map<String, Int>> = GenericDataTypes.ATTRIBUTES
    public val HOT_POTATO_BOOKS: DataType<Int> = GenericDataTypes.HOT_POTATO_BOOKS
    public val ART_OF_WAR: DataType<Boolean> = GenericDataTypes.ART_OF_WAR
    public val ART_OF_PEACE: DataType<Boolean> = GenericDataTypes.ART_OF_PEACE

    //? < 26.3 {
    /*@Deprecated("Removed DataType", ReplaceWith("GenericDataTypes.BOOSTER_TIERS"))
    @Suppress("DEPRECATION")
    public val BOOSTERS: DataType<List<String>> = GenericDataTypes.BOOSTERS*///?}
    public val BOOSTER_TIERS: DataType<Map<String, Int>> = GenericDataTypes.BOOSTER_TIERS
    public val JALAPENO_BOOK: DataType<Boolean> = GenericDataTypes.JALAPENO_BOOK
    public val MIDAS_WEAPON_BID: DataType<Int> = GenericDataTypes.MIDAS_WEAPON_BID
    public val MIDAS_WEAPON_ADDED_COINS: DataType<Int> = GenericDataTypes.MIDAS_WEAPON_ADDED_COINS
    public val MIDAS_WEAPON_PAID: DataType<Long> = GenericDataTypes.MIDAS_WEAPON_PAID
    public val ENRICHMENT: DataType<SkyBlockId> = GenericDataTypes.ENRICHMENT
    public val SOULBOUND: DataType<LoreDataTypes.SoulboundType> = LoreDataTypes.SOULBOUND
    public val ETHERMERGE: DataType<Boolean> = GenericDataTypes.ETHERMERGE
    public val TUNED_TRANSMISSION: DataType<Int> = GenericDataTypes.TUNED_TRANSMISSION


    // Misc (idfk)
    public val RIGHT_CLICK_MANA_ABILITY: DataType<Pair<String, Int>> = LoreDataTypes.RIGHT_CLICK_MANA_ABILITY
    public val COOLDOWN_ABILITY: DataType<Pair<String, Duration>> = LoreDataTypes.COOLDOWN_ABILITY
    public val QUIVER_ARROW: DataType<Boolean> = GenericDataTypes.QUIVER_ARROW
    public val SELECTED_ARROW: DataType<SkyBlockId> = LoreDataTypes.SELECTED_ARROW
    public val PERSONAL_COMPACTOR_ITEMS: DataType<List<String?>> = PersonalAccessoryDataTypes.PERSONAL_COMPACTOR_ITEMS
    public val PERSONAL_DELETOR_ITEMS: DataType<List<String?>> = PersonalAccessoryDataTypes.PERSONAL_DELETOR_ITEMS
    public val PERSONAL_ACCESSORY_ACTIVE: DataType<Boolean> = PersonalAccessoryDataTypes.PERSONAL_ACCESSORY_ACTIVE
    public val POTION: DataType<String> = GenericDataTypes.POTION
    public val POTION_TYPE: DataType<String> = GenericDataTypes.POTION_TYPE
    public val POTION_LEVEL: DataType<Int> = GenericDataTypes.POTION_LEVEL
    public val BOOK_OF_STATS: DataType<Int> = GenericDataTypes.BOOK_OF_STATS
    public val RUNEBOOK: DataType<Int> = GenericDataTypes.RUNEBOOK
    public val DOWSING_MODE: DataType<String> = GenericDataTypes.DOWSING_MODE
    public val HONEY_POT_USES: DataType<Int> = GenericDataTypes.HONEY_POT_USES

    public val USED_RUNE: DataType<SkyBlockId> = GenericDataTypes.USED_RUNE
    public val HELMET_SKIN: DataType<String> = GenericDataTypes.HELMET_SKIN
    public val APPLIED_DYE: DataType<String> = GenericDataTypes.APPLIED_DYE
    public val SHINY: DataType<Boolean> = GenericDataTypes.SHINY
    public val PET_DATA: DataType<PetData> = GenericDataTypes.PET_DATA
    public val SNOWBALLS: DataType<Pair<Int, Int>> = LoreDataTypes.SNOWBALLS
    public val ABSORB_LOGS: DataType<Long> = GenericDataTypes.ABSORB_LOGS
    public val LOGS_CUT: DataType<Long> = GenericDataTypes.LOGS_CUT
    public val GILDED_GIFTED_COINS: DataType<Long> = GenericDataTypes.GILDED_GIFTED_COINS
    public val ABICASE_MODEL: DataType<String> = GenericDataTypes.ABICASE_MODEL
    public val THUNDER_CHARGE: DataType<Int> = GenericDataTypes.THUNDER_CHARGE
    public val PELTS_EARNED: DataType<Long> = GenericDataTypes.PELTS_EARNED
    public val DONATED_MUSEUM: DataType<Boolean> = GenericDataTypes.DONATED_MUSEUM
    public val DAVID_CLOAK_UPGRADE: DataType<Int> = GenericDataTypes.DAVID_CLOAK_UPGRADE

    public val RAFFLE_WIN: DataType<String> = GenericDataTypes.RAFFLE_WIN
    public val RAFFLE_YEAR: DataType<Int> = GenericDataTypes.RAFFLE_YEAR

    public val DITTO_USED: DataType<Boolean> = GenericDataTypes.DITTO_USED
    public val DITTO_ITEM_ID: DataType<String> = GenericDataTypes.DITTO_ITEM_ID

    public val PARTY_HAT_COLOR: DataType<String> = GenericDataTypes.PARTY_HAT_COLOR
    public val PARTY_HAT_YEAR: DataType<Int> = GenericDataTypes.PARTY_HAT_YEAR

    public val RABBIT_FACTION: DataType<String> = GenericDataTypes.RABBIT_FACTION

    // Aging Items
    public val SECONDS_HELD: DataType<Int> = GenericDataTypes.SECONDS_HELD
    public val BOTTLE_OF_JYRRE_SECONDS: DataType<Int> = GenericDataTypes.BOTTLE_OF_JYRRE_SECONDS
    public val RIFT_DISCRITE_SECONDS: DataType<Int> = GenericDataTypes.RIFT_DISCRITE_SECONDS

    // Dungeons
    public val DUNGEON_ITEM: DataType<Boolean> = GenericDataTypes.DUNGEON_ITEM
    public val STAR_COUNT: DataType<Int> = GenericDataTypes.STAR_COUNT
    public val NECRON_SCROLLS: DataType<List<String>> = GenericDataTypes.NECRON_SCROLLS
    public val DUNGEON_TIER: DataType<Int> = GenericDataTypes.DUNGEON_TIER
    public val DUNGEON_QUALITY: DataType<Int> = GenericDataTypes.DUNGEON_QUALITY
    public val DUNGEONBREAKER_CHARGES: DataType<Pair<Int, Int>> = LoreDataTypes.DUNGEONBREAKER_CHARGES

    // Fishing Rod
    public val WET_BOOK: DataType<Int> = GenericDataTypes.WET_BOOK
    public val HOOK: DataType<Pair<UUID, String>> = GenericDataTypes.HOOK
    public val LINE: DataType<Pair<UUID, String>> = GenericDataTypes.LINE
    public val SINKER: DataType<Pair<UUID, String>> = GenericDataTypes.SINKER

    // Mining
    public val FUEL: DataType<Pair<Int, Int>> = LoreDataTypes.FUEL
    public val PICKONIMBUS_DURABILITY: DataType<Int> = MiningDataTypes.PICKONIMBUS_DURABILITY
    public val COMPACT_BLOCKS: DataType<Long> = MiningDataTypes.COMPACT_BLOCKS
    public val GEMSTONES: DataType<List<GemstoneSlotData>> = MiningDataTypes.GEMSTONES
    public val DIVAN_POWDER_COATING: DataType<Int> = MiningDataTypes.DIVAN_POWDER_COATING
    public val POLARVOID: DataType<Int> = MiningDataTypes.POLARVOID
    public val POWER_ABILITY_SCROLL: DataType<String> = MiningDataTypes.POWER_ABILITY_SCROLL
    public val FUEL_TANK: DataType<String> = MiningDataTypes.FUEL_TANK
    public val ENGINE: DataType<String> = MiningDataTypes.ENGINE
    public val UPGRADE_MODULE: DataType<String> = MiningDataTypes.UPGRADE_MODULE

    // Farming
    public val CULTIVATING_CROPS: DataType<Long> = GenericDataTypes.CULTIVATING_CROPS
    public val TOOL_LEVEL: DataType<Int> = GenericDataTypes.TOOL_LEVEL
    public val TOOL_EXP: DataType<Double> = GenericDataTypes.TOOL_EXP
    public val TOOL_OVERCLOCKS: DataType<Int> = GenericDataTypes.TOOL_OVERCLOCKS
    public val WATER_LEVEL: DataType<Pair<Int, Int>> = LoreDataTypes.WATER_LEVEL
    public val VINYLS: DataType<List<SkyBlockId>> = GenericDataTypes.VINYLS
}
