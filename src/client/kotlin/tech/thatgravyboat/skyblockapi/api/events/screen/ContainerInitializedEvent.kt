package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
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

    // TODO: find a better way to do this
    val notPlayerInventoryItems by lazy {
        val rows = rowCount ?: return@lazy emptyList()
        return@lazy itemStacks.subList(0, (rows * 9) - 1)
    }
}
