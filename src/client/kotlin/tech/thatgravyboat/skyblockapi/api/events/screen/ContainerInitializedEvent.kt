package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

class ContainerInitializedEvent(val itemStacks: List<ItemStack>, val titleComponent: Component) : SkyBlockEvent() {
    val title: String = titleComponent.stripped
}
