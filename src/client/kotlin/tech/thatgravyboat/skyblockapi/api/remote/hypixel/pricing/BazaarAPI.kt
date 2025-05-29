package tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing

import com.google.gson.JsonObject
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.utils.Scheduling
import tech.thatgravyboat.skyblockapi.utils.extentions.asDouble
import tech.thatgravyboat.skyblockapi.utils.extentions.asLong
import tech.thatgravyboat.skyblockapi.utils.extentions.asMap
import tech.thatgravyboat.skyblockapi.utils.http.Http
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

private const val URL = "https://api.hypixel.net/v2/skyblock/bazaar"

@Module
object BazaarAPI {

    var products = listOf<BazaarProduct>()
        private set

    fun getProduct(id: String?) = products.find { it.productId.equals(id, true) }

    init {
        Scheduling.schedule(0.seconds, 2.hours) {
            Http.getResult<JsonObject>(URL).let { res ->
                val response = res.getOrNull() ?: return@schedule
                products = response.getAsJsonObject("products").asMap { id, prod ->
                    val obj = prod.asJsonObject
                    val quick = obj.getAsJsonObject("quick_status")
                    id to BazaarProduct(
                        id,
                        quick.getAsJsonPrimitive("sellPrice").asDouble(0.0),
                        quick.getAsJsonPrimitive("sellVolume").asLong(0),
                        quick.getAsJsonPrimitive("buyPrice").asDouble(0.0),
                        quick.getAsJsonPrimitive("buyVolume").asLong(0),
                    )
                }.map { it.value }
            }
        }
    }

    data class BazaarProduct(
        val productId: String,
        val sellPrice: Double,
        val sellVolume: Long,
        val buyPrice: Double,
        val buyVolume: Long,
    )
}
