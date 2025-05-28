package tech.thatgravyboat.skyblockapi.api.item.calculator.sources

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.item.calculator.Calculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.DataTypeCalculator
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.Pricing

internal object AppliedDyeCalculator : DataTypeCalculator(DataTypes.APPLIED_DYE)
internal object HelmetSkinCalculator : DataTypeCalculator(DataTypes.HELMET_SKIN)

internal object AppliedRuneCalculator : Calculator {
    override fun calculate(id: String, stack: ItemStack): Long {
        val rune = stack.getData(DataTypes.APPLIED_RUNE) ?: return 0L
        return Pricing.getPrice("rune:${rune.first}:${rune.second}")
    }
}
