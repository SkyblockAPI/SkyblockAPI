package tech.thatgravyboat.skyblockapi.api.item.calculator.sources

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.item.calculator.CalculationEntry
import tech.thatgravyboat.skyblockapi.api.item.calculator.ItemEntry
import tech.thatgravyboat.skyblockapi.api.item.calculator.SingleEntryCalculator
import tech.thatgravyboat.skyblockapi.utils.extensions.getSkyBlockId

object BaseItemSource : SingleEntryCalculator {
    override fun getEntry(id: String, stack: ItemStack): CalculationEntry? {
        val id = stack.getSkyBlockId() ?: return null
        return ItemEntry(id)
    }
}
