package tech.thatgravyboat.skyblockapi.api.events.info

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.chunked
import tech.thatgravyboat.skyblockapi.utils.extentions.stripColor

data class TabListHeaderFooterChangeEvent(
    val oldFooter: Component,
    val oldHeader: Component,
    val newFooter: Component,
    val newHeader: Component,
) : SkyBlockEvent() {
    val newFooterChunked by lazy { newFooter.chunk() }
    val newHeaderChunked by lazy { newHeader.chunk() }
    val oldFooterChunked by lazy { oldFooter.chunk() }
    val oldHeaderChunked by lazy { oldHeader.chunk() }

    private fun Component.chunk() = string.split("\n")
        .chunked { it.stripColor().isBlank() }
        .map { it.filter { line -> line.isNotBlank() } }
}
