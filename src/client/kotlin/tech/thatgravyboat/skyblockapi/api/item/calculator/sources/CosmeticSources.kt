package tech.thatgravyboat.skyblockapi.api.item.calculator.sources

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.item.calculator.CalculationEntry
import tech.thatgravyboat.skyblockapi.api.item.calculator.DataTypeCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.ItemEntry
import tech.thatgravyboat.skyblockapi.api.item.calculator.SingleEntryCalculator

internal object AppliedDyeCalculator : DataTypeCalculator(DataTypes.APPLIED_DYE)
internal object HelmetSkinCalculator : DataTypeCalculator(DataTypes.HELMET_SKIN)

internal object AppliedRuneCalculator : SingleEntryCalculator {
    override fun getEntry(id: String, stack: ItemStack): CalculationEntry? {
        val rune = stack.getData(DataTypes.APPLIED_RUNE) ?: return null
        return ItemEntry("rune:${rune.first}:${rune.second}")
    }
}

internal object BookOfStatsCalculator : SingleEntryCalculator {
    override fun getEntry(id: String, stack: ItemStack): CalculationEntry? {
        stack.getData(DataTypes.BOOK_OF_STATS) ?: return null
        return ItemEntry("BOOK_OF_STATS")
    }
}
