package tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing

import com.google.gson.JsonObject
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.Scheduling
import tech.thatgravyboat.skyblockapi.utils.extentions.asDouble
import tech.thatgravyboat.skyblockapi.utils.extentions.asLong
import tech.thatgravyboat.skyblockapi.utils.extentions.asMap
import tech.thatgravyboat.skyblockapi.utils.http.Http
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

private const val URL = "https://skyblock-pv.thatgravyboat.tech/auctions"

@Module
object LowestBinAPI {
    /** Hypixel Item Id to Prices */
    var items = mapOf<String, AuctionItem>()
        private set

    fun getPrice(id: String?): AuctionItem? = items.entries.find { it.key.equals(id, ignoreCase = true) }?.value
    fun getLowestPrice(id: String?): Long? = getPrice(id)?.lowest

    init {
        Scheduling.schedule(0.seconds, 2.hours) {
            Http.getResult<JsonObject>(URL).let { res ->
                val response = res.getOrNull() ?: return@schedule
                items = response.asMap { id, item ->
                    val obj = item.asJsonObject
                    id to AuctionItem(
                        obj["lowest"].asLong(0),
                        obj["highest"].asLong(0),
                        obj["median"].asLong(0),
                        obj["mean"].asDouble(0.0),
                    )
                }
            }
        }
    }

    data class AuctionItem(
        val lowest: Long,
        val highest: Long,
        val median: Long,
        val mean: Double,
    )
}
