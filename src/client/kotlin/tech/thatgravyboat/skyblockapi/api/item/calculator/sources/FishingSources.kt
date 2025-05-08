package tech.thatgravyboat.skyblockapi.api.item.calculator.sources

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.item.calculator.Calculator
import tech.thatgravyboat.skyblockapi.api.remote.pricing.Pricing

internal object RodPartCalculator : Calculator {
    override fun calculate(id: String, stack: ItemStack): Long {
        val hookPrice = Pricing.getPrice(stack.getData(DataTypes.HOOK)?.second)
        val linePrice = Pricing.getPrice(stack.getData(DataTypes.LINE)?.second)
        val sinkerPrice = Pricing.getPrice(stack.getData(DataTypes.SINKER)?.second)

        return hookPrice + linePrice + sinkerPrice
    }
}

