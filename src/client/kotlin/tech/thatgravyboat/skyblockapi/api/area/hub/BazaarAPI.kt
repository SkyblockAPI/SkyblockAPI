package tech.thatgravyboat.skyblockapi.api.area.hub

import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.api.remote.pricing.BazaarAPI

@Deprecated("Moved to remote.pricing.BazaarAPI")
@ApiStatus.ScheduledForRemoval(inVersion = "1.21.6 or 1.22")
object BazaarAPI {

    val products get() = BazaarAPI.products
    fun getProduct(id: String?): BazaarProduct? {
        val product = BazaarAPI.getProduct(id) ?: return null

        return BazaarProduct(
            productId = product.productId,
            sellPrice = product.sellPrice,
            sellVolume = product.sellVolume,
            buyPrice = product.buyPrice,
            buyVolume = product.buyVolume,
        )
    }

    data class BazaarProduct(
        val productId: String,
        val sellPrice: Double,
        val sellVolume: Long,
        val buyPrice: Double,
        val buyVolume: Long,
    )
}
