package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.wardrobe.WardrobeData
import tech.thatgravyboat.skyblockapi.api.profile.wardrobe.WardrobeSlot

internal object WardrobeStorage {
    private val WARDROBE = StoredProfileData(
        ::WardrobeData,
        WardrobeData.CODEC,
        "wardrobe.json",
    )

    var currentSlot: Int?
        get() = WARDROBE.get()?.currentSlot.takeIf { it != -1 }
        private set(value) {
            WARDROBE.get()?.currentSlot = value ?: -1
        }

    var slots: MutableList<WardrobeSlot>
        get() = WARDROBE.get()?.slots ?: mutableListOf()
        private set(value) {
            WARDROBE.get()?.slots = value
        }


    fun updateCurrentSlot(slot: Int) {
        if (slot == currentSlot) return
        currentSlot = slot
        WARDROBE.save()
    }

    fun updateSlot(wardrobeSlot: WardrobeSlot) {
        slots = slots.filter { it.id != wardrobeSlot.id }.toMutableList().apply { add(wardrobeSlot) }
        slots.sortBy { it.id }
        WARDROBE.save()
    }

    fun clear() {
        currentSlot = null
        slots = mutableListOf()
        WARDROBE.save()
    }
}
