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

public class InventoryChangeEvent(
    public val item: ItemStack,
    public val slot: Slot,
    public val titleComponent: Component,
    public val inventory: List<Slot>,
    public val screen: AbstractContainerScreen<*>,
    public val previousItem: ItemStack,
) : SkyBlockEvent(), ItemDebugAttachable by item {
    public val isInPlayerInventory = slot.container is Inventory
    public val title = titleComponent.stripped
    public val itemStacks = inventory.map { it.item }

    public val isSkyBlockFiller = item.isSkyblockFiller()

    public val isInTopRow = slot.index < 9
    public val isInBottomRow = (screen as? ContainerScreenAccessor)?.containerRows?.let { (slot.index) >= (it - 1) * 9 } ?: false
    public val isOnLeftColumn = slot.index % 9 == 0
    public val isOnRightColumn = slot.index % 9 == 8

    public val isOnSides = isOnLeftColumn || isOnRightColumn
    public val isInTopRowOrBottomRow = isInTopRow || isInBottomRow
    public val isInMainPart = !isOnSides && !isInTopRowOrBottomRow
}
