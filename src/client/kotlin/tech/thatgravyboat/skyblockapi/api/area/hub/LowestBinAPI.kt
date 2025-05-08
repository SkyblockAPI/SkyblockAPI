package tech.thatgravyboat.skyblockapi.api.area.hub

import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.api.remote.pricing.LowestBinAPI
import tech.thatgravyboat.skyblockapi.api.remote.pricing.LowestBinAPI.AuctionItem

@Deprecated("Moved to remote.pricing.LowestBinAPI")
@ApiStatus.ScheduledForRemoval(inVersion = "1.21.6 or 1.22")
object LowestBinAPI {
    /** Hypixel Item Id to Prices */
    val items get() = LowestBinAPI.items

    fun getPrice(id: String?): AuctionItem? = LowestBinAPI.getPrice(id)
    fun getLowestPrice(id: String?): Long? = LowestBinAPI.getLowestPrice(id)
}
