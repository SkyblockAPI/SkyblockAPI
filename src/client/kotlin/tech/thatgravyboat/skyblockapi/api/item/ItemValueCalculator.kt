package tech.thatgravyboat.skyblockapi.api.item

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.area.hub.BazaarAPI
import tech.thatgravyboat.skyblockapi.api.area.hub.LowestBinAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.getId

internal object ItemValueCalculator {

    /** Use [tech.thatgravyboat.skyblockapi.api.datatype.getItemValue] to get the item value. */
    fun ItemStack.calculateItemValue(): ItemValueResult {
        val id = getId() ?: return ItemValueResult.EMPTY
        val lb = LowestBinAPI.getLowestPrice(id) ?: run {
            val price = BazaarAPI.getProduct(id)?.sellPrice?.toLong() ?: return ItemValueResult.EMPTY
            return ItemValueResult(price, price * this.count, emptyMap())
        }

        return ItemValueCalculatorSource.calculate(lb, this)
    }

}
