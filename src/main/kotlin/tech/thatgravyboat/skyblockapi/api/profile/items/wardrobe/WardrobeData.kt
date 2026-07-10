package tech.thatgravyboat.skyblockapi.api.profile.items.wardrobe

@Deprecated("Replace with WardrobeAPI", ReplaceWith("ech.thatgravyboat.skyblockapi.api.profile.items.loadout.WardrobeData"))
data class WardrobeData(
    var currentSlot: Int = -1,
    var slots: MutableList<WardrobeSlot> = mutableListOf(),
)

