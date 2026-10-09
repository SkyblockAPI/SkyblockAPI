package tech.thatgravyboat.skyblockapi.api.profile.items.wardrobe

//? < 26.3 {
/*import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.WardrobeData as NewWardrobeData

@Deprecated("Replace with WardrobeAPI", ReplaceWith("tech.thatgravyboat.skyblockapi.api.profile.items.loadout.WardrobeData"))
@Suppress("DEPRECATION")
public data class WardrobeData(
    var currentSlot: Int = -1,
    var slots: MutableList<WardrobeSlot> = mutableListOf(),
)

@Suppress("DEPRECATION")
internal fun NewWardrobeData.into() = WardrobeData(currentSlot, slots.mapTo(mutableListOf()) { it.into() })
*///?}
