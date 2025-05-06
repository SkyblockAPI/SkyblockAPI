package tech.thatgravyboat.skyblockapi.api.item.calculator.sources

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.item.calculator.Calculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.Pricing

internal object AppliedDyeCalculator : Calculator {
    override fun calculate(id: String, stack: ItemStack): Long {
        return Pricing.getPrice(stack.getData(DataTypes.APPLIED_DYE) ?: return 0L)
    }
}

internal object AppliedRuneCalculator : Calculator {
    override fun calculate(id: String, stack: ItemStack): Long {
        val rune = stack.getData(DataTypes.APPLIED_RUNE) ?: return 0L
        return Pricing.getPrice("rune:${rune.first}:${rune.second}")
    }
}

internal object HelmetSkinCalculator : Calculator {
    override fun calculate(id: String, stack: ItemStack): Long {
        return Pricing.getPrice(stack.getData(DataTypes.HELMET_SKIN) ?: return 0L)
    }
}
