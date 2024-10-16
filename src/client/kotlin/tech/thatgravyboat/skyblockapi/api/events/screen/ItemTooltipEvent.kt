package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

class ItemTooltipEvent(val item: ItemStack, val tooltip: MutableList<Component>) : SkyBlockEvent() {

    fun add(line: Component) = tooltip.add(line)

}

class ItemDebugTooltipEvent(val item: ItemStack, val tooltip: MutableList<Component>) : SkyBlockEvent() {

    fun add(line: Component) = tooltip.add(line)

}

