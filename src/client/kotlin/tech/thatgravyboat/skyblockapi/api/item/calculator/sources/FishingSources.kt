package tech.thatgravyboat.skyblockapi.api.item.calculator.sources

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.area.hub.LowestBinAPI
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.item.calculator.Calculator

internal object RodPartCalculator : Calculator {
    override fun calculate(stack: ItemStack): Long {
        val hook = stack.getData(DataTypes.HOOK)?.second
        val line = stack.getData(DataTypes.LINE)?.second
        val sinker = stack.getData(DataTypes.SINKER)?.second

        val hookPrice = LowestBinAPI.getLowestPrice(hook) ?: 0
        val linePrice = LowestBinAPI.getLowestPrice(line) ?: 0
        val sinkerPrice = LowestBinAPI.getLowestPrice(sinker) ?: 0

        return hookPrice + linePrice + sinkerPrice
    }
}

