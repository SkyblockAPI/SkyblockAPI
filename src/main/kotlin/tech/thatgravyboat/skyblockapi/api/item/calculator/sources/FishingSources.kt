package tech.thatgravyboat.skyblockapi.api.item.calculator.sources

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.item.calculator.CalculationEntry
import tech.thatgravyboat.skyblockapi.api.item.calculator.Calculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.IntDataTypeWithLimitCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.ItemEntry

internal object RodPartCalculator : Calculator {
    override fun calculate(id: String, stack: ItemStack): List<CalculationEntry>? {
        val data = listOf(DataTypes.HOOK, DataTypes.LINE, DataTypes.SINKER)
            .mapNotNull { stack.getData(it)?.second }
            .takeUnless { it.isEmpty() } ?: return null

        return data.map { ItemEntry(it) }
    }
}

internal object WetBookCalculator : IntDataTypeWithLimitCalculator(DataTypes.WET_BOOK, "WET_BOOK", 5)

