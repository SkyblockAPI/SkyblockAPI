package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.impl.debug.ItemDebugAttachable
import tech.thatgravyboat.skyblockapi.mixins.accessors.ContainerScreenAccessor
import tech.thatgravyboat.skyblockapi.utils.extentions.isSkyblockFiller
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

public data class InventoryChangeEvent(
    val item: ItemStack,
    val slot: Slot,
    val titleComponent: Component,
    val inventory: List<Slot>,
    val screen: AbstractContainerScreen<*>,
    val previousItem: ItemStack,
) : SkyBlockEvent(), ItemDebugAttachable by item {
    val isInPlayerInventory: Boolean = slot.container is Inventory
    val title: String = titleComponent.stripped
    val itemStacks: List<ItemStack> = inventory.map { it.item }

    val isSkyBlockFiller: Boolean = item.isSkyblockFiller()

    val isInTopRow: Boolean = slot.index < 9
    val isInBottomRow: Boolean = (screen as? ContainerScreenAccessor)?.containerRows?.let { (slot.index) >= (it - 1) * 9 } ?: false
    val isOnLeftColumn: Boolean = slot.index % 9 == 0
    val isOnRightColumn: Boolean = slot.index % 9 == 8

    val isOnSides: Boolean = isOnLeftColumn || isOnRightColumn
    val isInTopRowOrBottomRow: Boolean = isInTopRow || isInBottomRow
    val isInMainPart: Boolean = !isOnSides && !isInTopRowOrBottomRow
}
