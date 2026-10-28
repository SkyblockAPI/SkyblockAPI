package tech.thatgravyboat.skyblockapi.api.profile.maxwell

import net.minecraft.world.item.ItemStack

public data class AccessoryBagEntry(
    val item: ItemStack,
    val page: Int,
    val slot: Int,
)
