package tech.thatgravyboat.skyblockapi.api.profile.items.wardrobe

import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.item.ItemStack

@GenerateCodec
data class WardrobeSlot(
    val id: Int,
    val armor: MutableList<ItemStack>,
    val locked: Boolean,
)
