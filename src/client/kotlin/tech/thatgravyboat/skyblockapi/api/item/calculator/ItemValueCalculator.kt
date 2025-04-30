package tech.thatgravyboat.skyblockapi.api.item.calculator

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.area.hub.BazaarAPI
import tech.thatgravyboat.skyblockapi.api.area.hub.LowestBinAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.getId

internal object ItemValueCalculator {

    /** Use [tech.thatgravyboat.skyblockapi.api.datatype.getItemValue] to get the item value. */
    fun ItemStack.calculateItemValue(): ItemValueResult {
        val id = getId() ?: return ItemValueResult.EMPTY
        if (this.isBazaarItem()) {
            val bazaar = BazaarAPI.getProduct(id)?.sellPrice?.toLong() ?: return ItemValueResult.EMPTY
            return ItemValueResult(bazaar, bazaar * this.count, emptyMap())
        }
        val lb = LowestBinAPI.getLowestPrice(id) ?: 0L
        return ItemValueSource.calculate(lb, this)
    }

    fun ItemStack.isBazaarItem() = getId()?.let { BazaarAPI.getProduct(it) != null } ?: false

}
