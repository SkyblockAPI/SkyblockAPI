package tech.thatgravyboat.skyblockapi.api.area.hub

import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.remote.pricing.BazaarAPI as NewBazaarAPI

@RemoveNextVersion(
    replaceWith = ReplaceWith("BazaarAPI", "tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.BazaarAPI"),
)
object BazaarAPI {

    val products get() = NewBazaarAPI.products
    fun getProduct(id: String?): BazaarProduct? {
        val product = NewBazaarAPI.getProduct(id) ?: return null

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
