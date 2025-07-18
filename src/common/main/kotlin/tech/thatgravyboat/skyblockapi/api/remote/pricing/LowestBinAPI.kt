package tech.thatgravyboat.skyblockapi.api.remote.pricing

import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.LowestBinAPI as NewLowestBinAPI

@RemoveNextVersion(
    ReplaceWith(
        "LowestBinAPI",
        "tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing",
    ),
)
object LowestBinAPI {
    /** Hypixel Item Id to Prices */
    val items get() = NewLowestBinAPI.items

    fun getPrice(id: String?): AuctionItem? {
        val item = NewLowestBinAPI.getPrice(id) ?: return null

        return AuctionItem(
            lowest = item.lowest,
            highest = item.highest,
            median = item.median,
            mean = item.mean,
        )
    }

    fun getLowestPrice(id: String?): Long? = NewLowestBinAPI.getLowestPrice(id)

    data class AuctionItem(
        val lowest: Long,
        val highest: Long,
        val median: Long,
        val mean: Double,
    )
}
