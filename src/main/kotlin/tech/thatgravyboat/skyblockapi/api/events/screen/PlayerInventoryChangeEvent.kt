package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

public data class PlayerInventoryChangeEvent(
    val inventorySlot: Slot,
    val item: ItemStack,
    val previousItem: ItemStack,
) : SkyBlockEvent() {
    @RemoveNextVersion
    @Deprecated("Should pass previousItem as well")
    public constructor(inventorySlot: Slot, item: ItemStack) : this(inventorySlot, item, ItemStack.EMPTY)

    //? < 26.2
    //val slot: Int get() = slotIndex
    val slotIndex: Int get() = inventorySlot.index
}

public data class PlayerHotbarChangeEvent(
    val inventorySlot: Slot,
    val item: ItemStack,
    val previousItem: ItemStack,
) : SkyBlockEvent() {
    @RemoveNextVersion
    @Deprecated("Should pass previousItem as well")
    public constructor(inventorySlot: Slot, item: ItemStack) : this(inventorySlot, item, ItemStack.EMPTY)
    //? < 26.2
    //val slot: Int get() = slotIndex
    val slotIndex: Int get() = inventorySlot.index - FIRST_HOTBAR_SLOT

    public companion object {
        internal const val FIRST_HOTBAR_SLOT = 36
    }
}

public data class PlayerEquipmentChangeEvent(val entity: Player, val slot: EquipmentSlot, val previous: ItemStack, val current: ItemStack) : SkyBlockEvent() {
    public val item: ItemStack get() = current
}
