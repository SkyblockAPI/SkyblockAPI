package tech.thatgravyboat.skyblockapi.api.profile.wardrobe

import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.profile.items.wardrobe.WardrobeData as NewWardrobeData

@RemoveNextVersion
data class WardrobeData(
    var currentSlot: Int = -1,
    var slots: MutableList<WardrobeSlot> = mutableListOf(),
) {
    companion object {
        fun fromNewData(data: NewWardrobeData) = WardrobeData(
            currentSlot = data.currentSlot,
            slots = data.slots.map { WardrobeSlot.fromNewData(it) }.toMutableList(),
        )
    }
}

