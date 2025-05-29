package tech.thatgravyboat.skyblockapi.api.profile.accessory

import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.item.ItemStack

@GenerateCodec
data class AccessoryBagItem(
    val item: ItemStack,
    val page: Int,
    val slot: Int,
)
