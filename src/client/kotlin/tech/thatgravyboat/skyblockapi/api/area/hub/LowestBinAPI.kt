package tech.thatgravyboat.skyblockapi.api.area.hub

import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.api.remote.pricing.LowestBinAPI

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
