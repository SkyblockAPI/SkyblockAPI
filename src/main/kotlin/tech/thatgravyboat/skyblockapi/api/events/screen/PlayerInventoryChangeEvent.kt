package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

data class PlayerInventoryChangeEvent(val inventorySlot: Slot, val item: ItemStack) : SkyBlockEvent() {
    val slotIndex get() = inventorySlot.index
}

data class PlayerHotbarChangeEvent(val inventorySlot: Slot, val item: ItemStack) : SkyBlockEvent() {
    val slotIndex get() = inventorySlot.index - FIRST_HOTBAR_SLOT

    companion object {
        internal const val FIRST_HOTBAR_SLOT = 36
    }
}

data class PlayerEquipmentChangeEvent(val entity: Player, val slot: EquipmentSlot, val previous: ItemStack, val current: ItemStack) : SkyBlockEvent() {
    val item get() = current
}
