package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

// TODO: create inventory event abstract class? (maybe even interface for slot events?)
class SlotClickEvent(
    val item: ItemStack,
    val slot: Slot,
    val button: Int,
    val screen: AbstractContainerScreen<*>,
) : CancellableSkyBlockEvent() {
    val titleComponent: Component = screen.title
    val title: String = titleComponent.stripped
    val slots: List<Slot> = screen.menu.slots
    val menuSlots: List<Slot> = screen.menu.slots.filter { it.container !is Inventory }
    val isInPlayerInventory = slot.container is Inventory
}
