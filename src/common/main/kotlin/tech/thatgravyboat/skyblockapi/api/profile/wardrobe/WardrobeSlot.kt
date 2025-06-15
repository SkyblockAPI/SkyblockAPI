package tech.thatgravyboat.skyblockapi.api.profile.wardrobe

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.profile.items.wardrobe.WardrobeSlot as NewWardrobeSlot

@RemoveNextVersion
data class WardrobeSlot(
    val id: Int,
    val armor: MutableList<ItemStack>,
    val locked: Boolean,
) {
    companion object {
        fun fromNewData(data: NewWardrobeSlot) = WardrobeSlot(
            id = data.id,
            armor = data.armor,
            locked = false,
        )
    }
}
