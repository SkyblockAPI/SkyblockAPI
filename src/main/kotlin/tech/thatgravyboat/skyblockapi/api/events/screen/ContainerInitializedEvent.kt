package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.mixins.accessors.ContainerScreenAccessor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

public class ContainerInitializedEvent(
    public val itemStacks: List<ItemStack>,
    public val screen: AbstractContainerScreen<*>
) : SkyBlockEvent() {

    public val titleComponent: Component = screen.title
    public val title: String = titleComponent.stripped
    public val rowCount: Int? = (screen as? ContainerScreenAccessor)?.containerRows

    public val containerSlots: List<Slot> = screen.menu.slots.takeWhile { it.container !is Inventory }
    public val containerItems: List<ItemStack> = containerSlots.map { it.item }
}
