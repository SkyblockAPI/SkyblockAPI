package tech.thatgravyboat.skyblockapi.api.profile.items.wardrobe

//? < 26.3 {
/*import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.ArmorWardrobeAPI as NewWardrobeAPI

@Deprecated("Replace with ArmorWardrobeAPI", ReplaceWith("ech.thatgravyboat.skyblockapi.api.profile.items.loadout.ArmorWardrobeAPI"))
public object WardrobeAPI {
    public val inWardrobe: Boolean get() = NewWardrobeAPI.inWardrobe

    /** 0 if not in wardrobe */
    public val currentPage: Int get() = NewWardrobeAPI.currentPage

    public val slots: List<WardrobeSlot> get() = NewWardrobeAPI.slots.map { it.into() }
    public val currentSlot: Int? get() = NewWardrobeAPI.currentSlot

    public fun isCurrentSlotInCurrentPage(): Unit = NewWardrobeAPI.isCurrentSlotInCurrentPage()
}*///?}
