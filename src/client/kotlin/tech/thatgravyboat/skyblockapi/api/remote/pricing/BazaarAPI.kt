package tech.thatgravyboat.skyblockapi.api.remote.pricing

import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.BazaarAPI
import com.google.gson.JsonObject
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.utils.Scheduling
import tech.thatgravyboat.skyblockapi.utils.extentions.asDouble
import tech.thatgravyboat.skyblockapi.utils.extentions.asLong
import tech.thatgravyboat.skyblockapi.utils.extentions.asMap
import tech.thatgravyboat.skyblockapi.utils.http.Http
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

@Deprecated("Moved to remote.hypixel.pricing.BazaarAPI")
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
