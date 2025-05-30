package tech.thatgravyboat.skyblockapi.api.profile.wardrobe

import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.profile.items.wardrobe.WardrobeAPI as NewWardrobeAPI

@RemoveNextVersion
object WardrobeAPI {
    val inWardrobe get() = NewWardrobeAPI.inWardrobe

    val currentPage get() = NewWardrobeAPI.currentPage

    val slots: List<WardrobeSlot> get() = NewWardrobeAPI.slots.map(WardrobeSlot::fromNewData)
    val currentSlot: Int? get() = NewWardrobeAPI.currentSlot

    fun isCurrentSlotInCurrentPage() = NewWardrobeAPI.isCurrentSlotInCurrentPage()
}
