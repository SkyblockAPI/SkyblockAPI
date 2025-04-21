package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

class InventoryChangeEvent(
    val item: ItemStack,
    val slot: Slot,
    val titleComponent: Component,
    val inventory: List<Slot>,
    val screen: AbstractContainerScreen<*>,
) : SkyBlockEvent() {
    val isInPlayerInventory = slot.container is Inventory
    val title = titleComponent.stripped
}
