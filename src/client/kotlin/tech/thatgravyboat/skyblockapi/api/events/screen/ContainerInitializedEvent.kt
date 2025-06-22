package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.mixins.accessors.ContainerScreenAccessor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

class ContainerInitializedEvent(
    val itemStacks: List<ItemStack>,
    val screen: AbstractContainerScreen<*>
) : SkyBlockEvent() {

    val titleComponent: Component = screen.title
    val title: String = titleComponent.stripped
    val rowCount: Int? = (screen as? ContainerScreenAccessor)?.containerRows

    val containerSlots: List<Slot> = screen.menu.slots.takeWhile { it.container !is Inventory }
    val containerItems: List<ItemStack> = containerSlots.map { it.item }
}
