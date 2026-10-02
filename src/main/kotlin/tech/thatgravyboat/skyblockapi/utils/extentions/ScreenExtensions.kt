package tech.thatgravyboat.skyblockapi.utils.extentions

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.item.ItemStack

fun AbstractContainerScreen<*>.getColumn(column: Int): List<ItemStack> {
    return this.menu.slots.filterIndexed { index, _ -> index % this.containerWidth == column }.map { it.item }
}
