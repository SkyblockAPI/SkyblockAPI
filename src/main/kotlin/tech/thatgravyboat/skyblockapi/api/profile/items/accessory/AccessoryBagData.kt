package tech.thatgravyboat.skyblockapi.api.profile.items.accessory

import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.item.ItemStack

@GenerateCodec
public data class AccessoryBagItem(
    val item: ItemStack,
    val page: Int,
    val slot: Int,
)
