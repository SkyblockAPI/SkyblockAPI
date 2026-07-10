package tech.thatgravyboat.skyblockapi.api.profile.items.wardrobe

import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.WardrobeSlot
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.ArmorWardrobeAPI as NewWardrobeAPI

@Deprecated("Replace with ArmorWardrobeAPI", ReplaceWith("ech.thatgravyboat.skyblockapi.api.profile.items.loadout.ArmorWardrobeAPI"))
object WardrobeAPI {
    val inWardrobe get() = NewWardrobeAPI.inWardrobe

    /** 0 if not in wardrobe */
    val currentPage get() = NewWardrobeAPI.currentPage

    val slots get() = NewWardrobeAPI.slots.map { WardrobeSlot(it.id, it.slots, it.locked) }
    val currentSlot: Int? get() = NewWardrobeAPI.currentSlot

    fun isCurrentSlotInCurrentPage() = NewWardrobeAPI.isCurrentSlotInCurrentPage()
}
