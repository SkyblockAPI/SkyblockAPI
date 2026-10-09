package tech.thatgravyboat.skyblockapi.api.item.calculator

import net.minecraft.world.item.ItemStack
import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.Pricing
import tech.thatgravyboat.skyblockapi.utils.extentions.getSkyBlockId

internal interface ItemValueItemStack {

    fun `skyblockapi$getItemValueResult`(): ItemValueResult?
}

public fun ItemStack.getItemValue(): ItemValueResult = (this as? ItemValueItemStack)?.`skyblockapi$getItemValueResult`() ?: ItemValueResult.EMPTY

public object ItemValueCalculator {
    /** Use [tech.thatgravyboat.skyblockapi.api.item.calculator.getItemValue] to get the item value. */
    @JvmStatic
    @ApiStatus.Internal
    public fun calculateItemValue(stack: ItemStack): ItemValueResult {
        val id = stack.getSkyBlockId() ?: return ItemValueResult.EMPTY
        return ItemValueSource.calculate(Pricing.getPrice(id), stack)
    }
}
