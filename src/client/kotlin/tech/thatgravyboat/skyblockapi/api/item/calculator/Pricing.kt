package tech.thatgravyboat.skyblockapi.api.item.calculator

import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.api.remote.pricing.BazaarAPI
import tech.thatgravyboat.skyblockapi.api.remote.pricing.LowestBinAPI

@Deprecated("Moved", ReplaceWith("remote.pricing.Pricing.getPrice(id)"))
@ApiStatus.ScheduledForRemoval(inVersion = "1.21.6 or 1.22")
object Pricing {
    fun getPrice(id: String?): Long {
        val product = BazaarAPI.getProduct(id)
        if (product != null) {
            return product.sellPrice.toLong()
        }
        return LowestBinAPI.getLowestPrice(id) ?: 0L
    }
}
