package tech.thatgravyboat.skyblockapi.api.profile.maxwell

//? if < 26.4 {
import net.minecraft.world.item.ItemStack

@Deprecated("Accessories aren't really assigned a specific slot and page anymore.")
public data class AccessoryBagEntry(
    val item: ItemStack,
    val page: Int,
    val slot: Int,
)//?}
