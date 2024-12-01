package tech.thatgravyboat.skyblockapi.api.events.info

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.chunked
import tech.thatgravyboat.skyblockapi.utils.extentions.stripColorAndS

data class TabListHeaderFooterChangeEvent(
    val oldFooter: Component,
    val oldHeader: Component,
    val newFooter: Component,
    val newHeader: Component,
) : SkyBlockEvent() {
    val newFooterChunked = newFooter.chunk()
    val newHeaderChunked = newHeader.chunk()
    val oldFooterChunked = oldFooter.chunk()
    val oldHeaderChunked = oldHeader.chunk()

    private fun Component.chunk() = string.split("\n").chunked { it.stripColorAndS().isBlank() }
}
