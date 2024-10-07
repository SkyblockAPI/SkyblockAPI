package tech.thatgravyboat.skyblockapi.api.datatype

import kotlinx.datetime.Instant
import tech.thatgravyboat.skyblockapi.api.data.SkyblockCategory
import tech.thatgravyboat.skyblockapi.api.data.SkyblockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.GenericDataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.LoreDataTypes
import kotlin.time.Duration

object DataTypes {

    val ID: DataType<String> = GenericDataTypes.ID
    val RARITY: DataType<SkyblockRarity> = LoreDataTypes.RARITY
    val CATEGORY: DataType<SkyblockCategory> = LoreDataTypes.CATEGORY

    val MODIFIER: DataType<String> = GenericDataTypes.MODIFIER
    val RARITY_UPGRADES: DataType<Int> = GenericDataTypes.RARITY_UPGRADES
    val FUEL: DataType<Pair<Int, Int>> = LoreDataTypes.FUEL
    val SNOWBALLS: DataType<Pair<Int, Int>> = LoreDataTypes.SNOWBALLS
    val RIGHT_CLICK_MANA_ABILITY: DataType<Pair<String, Int>> = LoreDataTypes.RIGHT_CLICK_MANA_ABILITY
    val COOLDOWN_ABILITY: DataType<Pair<String, Duration>> = LoreDataTypes.COOLDOWN_ABILITY
    val TIMESTAMP: DataType<Instant> = GenericDataTypes.TIMESTAMP
    val SECONDS_HELD: DataType<Int> = GenericDataTypes.SECONDS_HELD
}
