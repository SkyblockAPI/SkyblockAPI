package tech.thatgravyboat.skyblockapi.api.area.hub

import com.google.gson.JsonObject
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.Scheduling
import tech.thatgravyboat.skyblockapi.utils.extentions.asMap
import tech.thatgravyboat.skyblockapi.utils.http.Http
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

private const val URL = "https://api.hypixel.net/v2/skyblock/bazaar"

@Module
object BazaarAPI {

    var products = listOf<BazaarProduct>()
        private set

    init {
        Scheduling.schedule(0.seconds, 2.hours) {
            fetch()
        }
    }

    private suspend fun fetch() {
        Http.getResult<JsonObject>(URL).let { res ->
            val response = res.getOrNull() ?: return
            products = response.getAsJsonObject("products").asMap { id, prod ->
                val obj = prod.asJsonObject
                val quick = obj.getAsJsonObject("quick_status")
                id to BazaarProduct(
                    id,
                    quick.getAsJsonPrimitive("sellPrice").asDouble,
                    quick.getAsJsonPrimitive("sellVolume").asLong,
                    quick.getAsJsonPrimitive("buyPrice").asDouble,
                    quick.getAsJsonPrimitive("buyVolume").asLong,
                )
            }.map { it.value }
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
