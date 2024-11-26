package tech.thatgravyboat.skyblockapi.api.profile.wardrobe

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec

@GenerateCodec
data class WardrobeSlot(
    val id: Int,
    val armor: MutableList<ItemStack>,
    val locked: Boolean,
)
