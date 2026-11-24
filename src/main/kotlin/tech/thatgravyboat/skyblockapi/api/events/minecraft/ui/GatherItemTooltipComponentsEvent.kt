package tech.thatgravyboat.skyblockapi.api.events.minecraft.ui

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

public data class GatherItemTooltipComponentsEvent(val item: ItemStack, val components: MutableList<ClientTooltipComponent>) : SkyBlockEvent() {

    public fun add(component: ClientTooltipComponent): Boolean = components.add(component)
    public fun add(component: Component): Boolean = components.add(ClientTooltipComponent.create(component.visualOrderText))
}
