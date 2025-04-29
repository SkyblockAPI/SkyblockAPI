package tech.thatgravyboat.skyblockapi.api.item

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.area.hub.BazaarAPI
import tech.thatgravyboat.skyblockapi.api.area.hub.LowestBinAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.getId

object ItemValueCalculator {

    fun ItemStack.calculateItemValue(): ItemValueResult {
        val id = getId() ?: return ItemValueResult.EMPTY
        val lb = LowestBinAPI.items[id]?.lowest ?: run {
            val price = BazaarAPI.getProduct(id)?.sellPrice?.toLong() ?: return ItemValueResult.EMPTY
            return ItemValueResult(price, price * this.count, emptyMap())
        }

        return ItemValueCalculatorSource.calculate(lb, this)
    }

}
