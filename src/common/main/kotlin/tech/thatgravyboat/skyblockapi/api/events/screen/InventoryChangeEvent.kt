package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.impl.tagkey.ItemTag
import tech.thatgravyboat.skyblockapi.mixins.accessors.ContainerScreenAccessor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

class InventoryChangeEvent(
    val item: ItemStack,
    val slot: Slot,
    val titleComponent: Component,
    val inventory: List<Slot>,
    val screen: AbstractContainerScreen<*>,
) : SkyBlockEvent() {
    val isInPlayerInventory = slot.container is Inventory
    val title = titleComponent.stripped
    val itemStacks = inventory.map { it.item }

    val isSkyBlockFiller = item.isEmpty || item in ItemTag.GLASS_PANES

    val isInTopRow = slot.index < 9
    val isInBottomRow = (screen as? ContainerScreenAccessor)?.containerRows?.let { (slot.index) >= (it - 1) * 9 } ?: false
    val isOnLeftColumn = slot.index % 9 == 0
    val isOnRightColumn = slot.index % 9 == 8

    val isOnSides = isOnLeftColumn || isOnRightColumn
    val isInTopRowOrBottomRow = isInTopRow || isInBottomRow
    val isInMainPart = !isOnSides && !isInTopRowOrBottomRow
}
