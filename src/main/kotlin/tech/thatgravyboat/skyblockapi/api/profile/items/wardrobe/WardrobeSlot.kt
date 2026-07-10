package tech.thatgravyboat.skyblockapi.api.profile.items.wardrobe

import net.minecraft.world.item.ItemStack

@Deprecated("Replace with WardrobeAPI", ReplaceWith("tech.thatgravyboat.skyblockapi.api.profile.items.loadout.WardrobeSlot"))
data class WardrobeSlot(
    val id: Int,
    val armor: MutableList<ItemStack>,
    val locked: Boolean,
)
