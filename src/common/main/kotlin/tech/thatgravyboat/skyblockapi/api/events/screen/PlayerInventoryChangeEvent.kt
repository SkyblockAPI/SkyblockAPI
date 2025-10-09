package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

class PlayerInventoryChangeEvent(val slot: Int, val item: ItemStack) : SkyBlockEvent()

class PlayerHotbarChangeEvent(val slot: Int, val item: ItemStack) : SkyBlockEvent()

class PlayerEquipmentChangeEvent(val entity: Player, val slot: EquipmentSlot, val previous: ItemStack, val current: ItemStack) : SkyBlockEvent() {
    val item get() = current
}
