package tech.thatgravyboat.skyblockapi.api.remote.pricing

import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.LowestBinAPI
import com.google.gson.JsonObject
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.utils.Scheduling
import tech.thatgravyboat.skyblockapi.utils.extentions.asDouble
import tech.thatgravyboat.skyblockapi.utils.extentions.asLong
import tech.thatgravyboat.skyblockapi.utils.extentions.asMap
import tech.thatgravyboat.skyblockapi.utils.http.Http
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

@Deprecated("Moved to remote.hypixel.pricing.LowestBinAPI")
@ApiStatus.ScheduledForRemoval(inVersion = "1.21.6 or 1.22")
object LowestBinAPI {
    /** Hypixel Item Id to Prices */
    val items get() = LowestBinAPI.items

    fun getPrice(id: String?): AuctionItem? {
        val item = LowestBinAPI.getPrice(id) ?: return null

        return AuctionItem(
            lowest = item.lowest,
            highest = item.highest,
            median = item.median,
            mean = item.mean,
        )
    }

    fun getLowestPrice(id: String?): Long? = LowestBinAPI.getLowestPrice(id)

    data class AuctionItem(
        val lowest: Long,
        val highest: Long,
        val median: Long,
        val mean: Double,
    )
}
