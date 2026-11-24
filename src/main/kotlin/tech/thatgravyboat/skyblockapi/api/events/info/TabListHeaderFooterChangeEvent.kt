package tech.thatgravyboat.skyblockapi.api.events.info

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.chunked
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

public typealias TabListHeaderFooterUpdateEvent = TabListHeaderFooterChangeEvent

public data class TabListHeaderFooterChangeEvent(
    val oldFooter: Component,
    val oldHeader: Component,
    val newFooter: Component,
    val newHeader: Component,
) : SkyBlockEvent() {
    public val newFooterChunked: List<List<String>> by lazy { newFooter.chunk() }
    public val newHeaderChunked: List<List<String>> by lazy { newHeader.chunk() }
    public val oldFooterChunked: List<List<String>> by lazy { oldFooter.chunk() }
    public val oldHeaderChunked: List<List<String>> by lazy { oldHeader.chunk() }

    private fun Component.chunk() = stripped.split("\n")
        .chunked(CharSequence::isBlank)
        .map { it.filter(CharSequence::isNotBlank) }
        .filter(List<String>::isNotEmpty)
}
