package tech.thatgravyboat.skyblockapi.api.profile.accessory

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.RemoveNextVersion

@RemoveNextVersion
data class AccessoryBagItem(
    val item: ItemStack,
    val page: Int,
    val slot: Int,
)
