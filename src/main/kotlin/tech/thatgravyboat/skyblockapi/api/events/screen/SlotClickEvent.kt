package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

// TODO: create inventory event abstract class? (maybe even interface for slot events?)
public class SlotClickEvent(
    public val item: ItemStack,
    public val slot: Slot,
    public val button: Int,
    public val screen: AbstractContainerScreen<*>,
) : CancellableSkyBlockEvent() {
    public val titleComponent: Component = screen.title
    public val title: String = titleComponent.stripped
    public val slots: List<Slot> = screen.menu.slots
    public val menuSlots: List<Slot> = screen.menu.slots.filter { it.container !is Inventory }
    public val isInPlayerInventory = slot.container is Inventory
}
