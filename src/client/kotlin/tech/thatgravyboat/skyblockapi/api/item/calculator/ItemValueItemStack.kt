package tech.thatgravyboat.skyblockapi.api.item.calculator

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.Pricing
import tech.thatgravyboat.skyblockapi.utils.extentions.getSkyBlockId

internal interface ItemValueItemStack {

    fun `skyblockapi$getItemValueResult`(): ItemValueResult?
}

fun ItemStack.getItemValue(): ItemValueResult = (this as? ItemValueItemStack)?.`skyblockapi$getItemValueResult`() ?: ItemValueResult.EMPTY

object ItemValueCalculator {
    /** Use [tech.thatgravyboat.skyblockapi.api.item.calculator.getItemValue] to get the item value. */
    @JvmStatic
    internal fun calculateItemValue(stack: ItemStack): ItemValueResult {
        val id = stack.getSkyBlockId() ?: return ItemValueResult.EMPTY
        return ItemValueSource.calculate(Pricing.getPrice(id), stack)
    }
}
