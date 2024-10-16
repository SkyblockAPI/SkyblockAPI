package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

class ContainerChangeEvent(
    val item: ItemStack,
    val slot: Int,
    val titleComponent: Component,
    val inventory: List<ItemStack>
) : SkyBlockEvent() {
    val title = titleComponent.stripped
}
