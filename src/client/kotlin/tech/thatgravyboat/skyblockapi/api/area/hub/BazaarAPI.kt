package tech.thatgravyboat.skyblockapi.api.area.hub

import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.remote.pricing.BazaarAPI

@RemoveNextVersion(
    replaceWith = ReplaceWith("BazaarAPI", "tech.thatgravyboat.skyblockapi.api.remote.pricing.BazaarAPI"),
)
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
