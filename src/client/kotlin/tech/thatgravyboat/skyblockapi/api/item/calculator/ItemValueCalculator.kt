package tech.thatgravyboat.skyblockapi.api.item.calculator

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.area.hub.BazaarAPI
import tech.thatgravyboat.skyblockapi.api.area.hub.LowestBinAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.getId

internal object ItemValueCalculator {

    /** Use [tech.thatgravyboat.skyblockapi.api.item.calculator.getItemValue] to get the item value. */
    @JvmStatic
    fun ItemStack.calculateItemValue(): ItemValueResult {
        val id = getId() ?: return ItemValueResult.EMPTY
        val product = BazaarAPI.getProduct(id)
        if (product != null) {
            val bazaar = product.sellPrice.toLong()
            return ItemValueResult(bazaar, bazaar * this.count, emptyMap())
        }
        val lb = LowestBinAPI.getLowestPrice(id) ?: 0L
        return ItemValueSource.calculate(lb, this)
    }

}
