package tech.thatgravyboat.skyblockapi.api.area.hub

import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.remote.pricing.LowestBinAPI

@RemoveNextVersion(
    replaceWith = ReplaceWith("LowestBinAPI", "tech.thatgravyboat.skyblockapi.api.remote.pricing.LowestBinAPI"),
)
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
