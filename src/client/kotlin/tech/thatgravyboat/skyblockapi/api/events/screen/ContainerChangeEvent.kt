package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

//TODO remove when updating to new version
@Deprecated("Will be removed when the next minecraft update (1.22/1.21.6) releases!", replaceWith = ReplaceWith("InventoryChangeEvent"))
class ContainerChangeEvent(
    val item: ItemStack,
    val slot: Int,
    val screen: AbstractContainerScreen<*>,
    val inventory: List<ItemStack>,
) : SkyBlockEvent() {
    val titleComponent: Component = screen.title
    val title = titleComponent.stripped
}

class InventoryChangeEvent(
    val item: ItemStack,
    val slot: Slot,
    val titleComponent: Component,
) : SkyBlockEvent() {
    val isInPlayerInventory = slot.container is Inventory
    val title = titleComponent.stripped
}
