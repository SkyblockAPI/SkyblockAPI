package tech.thatgravyboat.skyblockapi.api.profile.items.loadout

import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.item.ItemStack

@GenerateCodec
data class WardrobeSlot(
    val id: Int,
    val slots: MutableList<ItemStack>,
    val locked: Boolean,
)
