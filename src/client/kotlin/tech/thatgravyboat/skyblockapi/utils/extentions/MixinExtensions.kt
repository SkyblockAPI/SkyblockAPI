package tech.thatgravyboat.skyblockapi.utils.extentions

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.Slot
import tech.thatgravyboat.skyblockapi.mixins.accessors.AbstractContainerScreenAccessor

/**
 * This contains extensions of which accessors or interfaces are injected on vanilla classes.
 */


fun AbstractContainerScreen<*>.getHoveredSlot(): Slot? =
    (this as AbstractContainerScreenAccessor).hoveredSlot
