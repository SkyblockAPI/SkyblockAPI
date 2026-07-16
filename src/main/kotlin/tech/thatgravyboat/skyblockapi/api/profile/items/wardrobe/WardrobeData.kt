package tech.thatgravyboat.skyblockapi.api.profile.items.wardrobe

import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.WardrobeData as NewWardrobeData

@Deprecated("Replace with WardrobeAPI", ReplaceWith("tech.thatgravyboat.skyblockapi.api.profile.items.loadout.WardrobeData"))
data class WardrobeData(
    var currentSlot: Int = -1,
    var slots: MutableList<WardrobeSlot> = mutableListOf(),
)

internal fun NewWardrobeData.into() = WardrobeData(currentSlot, slots.mapTo(mutableListOf()) { it.into() })

