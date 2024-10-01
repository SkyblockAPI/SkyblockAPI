package tech.thatgravyboat.skyblockapi.api.datatype

import tech.thatgravyboat.skyblockapi.api.data.SkyblockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.GenericDataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.LoreDataTypes
import kotlin.time.Duration

object DataTypes {

    val ID: DataType<String> = GenericDataTypes.ID
    val RARITY: DataType<SkyblockRarity> = LoreDataTypes.RARITY

    val MODIFIER: DataType<String> = GenericDataTypes.MODIFIER
    val FUEL: DataType<Pair<Int, Int>> = LoreDataTypes.FUEL
    val RIGHT_CLICK_MANA_ABILITY: DataType<Pair<String, Int>> = LoreDataTypes.RIGHT_CLICK_MANA_ABILITY
    val COOLDOWN_ABILITY: DataType<Pair<String, Duration>> = LoreDataTypes.COOLDOWN_ABILITY
}
