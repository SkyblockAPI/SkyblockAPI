package tech.thatgravyboat.skyblockapi.api.datatype

import kotlinx.datetime.Instant
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockCategory
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.GemstoneSlotData
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.GenericDataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.GenericDataTypes.PetData
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.LoreDataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.PersonalAccessoryDataTypes
import java.util.*
import kotlin.time.Duration

object DataTypes {

    val ID: DataType<String> = GenericDataTypes.ID
    val API_ID: DataType<String> = GenericDataTypes.API_ID
    val UUID: DataType<UUID> = GenericDataTypes.UUID
    val RARITY: DataType<SkyBlockRarity> = LoreDataTypes.RARITY
    val CATEGORY: DataType<SkyBlockCategory> = LoreDataTypes.CATEGORY

    val MODIFIER: DataType<String> = GenericDataTypes.MODIFIER
    val RARITY_UPGRADES: DataType<Int> = GenericDataTypes.RARITY_UPGRADES
    val FUEL: DataType<Pair<Int, Int>> = LoreDataTypes.FUEL
    val SNOWBALLS: DataType<Pair<Int, Int>> = LoreDataTypes.SNOWBALLS
    val RIGHT_CLICK_MANA_ABILITY: DataType<Pair<String, Int>> = LoreDataTypes.RIGHT_CLICK_MANA_ABILITY
    val COOLDOWN_ABILITY: DataType<Pair<String, Duration>> = LoreDataTypes.COOLDOWN_ABILITY
    val TIMESTAMP: DataType<Instant> = GenericDataTypes.TIMESTAMP
    val SECONDS_HELD: DataType<Int> = GenericDataTypes.SECONDS_HELD
    val PICKONIMBUS_DURABILITY: DataType<Int> = GenericDataTypes.PICKONIMBUS_DURABILITY
    val QUIVER_ARROW: DataType<Boolean> = GenericDataTypes.QUIVER_ARROW
    val PERSONAL_COMPACTOR_ITEMS: DataType<List<String?>> = PersonalAccessoryDataTypes.PERSONAL_COMPACTOR_ITEMS
    val PERSONAL_DELETOR_ITEMS: DataType<List<String?>> = PersonalAccessoryDataTypes.PERSONAL_DELETOR_ITEMS
    val PERSONAL_ACCESSORY_ACTIVE: DataType<Boolean> = PersonalAccessoryDataTypes.PERSONAL_ACCESSORY_ACTIVE
    val ENCHANTMENTS: DataType<Map<String, Int>> = GenericDataTypes.ENCHANTMENTS
    val HOT_POTATO_BOOKS: DataType<Int> = GenericDataTypes.HOT_POTATO_BOOKS
    val GEMSTONES: DataType<List<GemstoneSlotData>> = GenericDataTypes.GEMSTONES
    val POTION: DataType<String> = GenericDataTypes.POTION
    val POTION_LEVEL: DataType<Int> = GenericDataTypes.POTION_LEVEL
    val ATTRIBUTES: DataType<Map<String, Int>> = GenericDataTypes.ATTRIBUTES
    val CROPS_BROKEN: DataType<Long> = GenericDataTypes.CROPS_BROKEN
    val COMPACT_BLOCKS: DataType<Long> = GenericDataTypes.COMPACT_BLOCKS
    val STAR_COUNT: DataType<Int> = GenericDataTypes.STAR_COUNT
    val DUNGEON_ITEM: DataType<Boolean> = GenericDataTypes.DUNGEON_ITEM
    val APPLIED_RUNE: DataType<Pair<String, Int>> = GenericDataTypes.APPLIED_RUNE
    val PET_DATA: DataType<PetData> = GenericDataTypes.PET_DATA

    // Fishing Rod
    val HOOK: DataType<Pair<UUID, String>> = GenericDataTypes.HOOK
    val LINE: DataType<Pair<UUID, String>> = GenericDataTypes.LINE
    val SINKER: DataType<Pair<UUID, String>> = GenericDataTypes.SINKER

    // Drill Components
    val FUEL_TANK: DataType<String> = GenericDataTypes.FUEL_TANK
    val ENGINE: DataType<String> = GenericDataTypes.ENGINE
    val UPGRADE_MODULE: DataType<String> = GenericDataTypes.UPGRADE_MODULE
}
