package tech.thatgravyboat.skyblockapi.utils.extentions

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.Slot
import tech.thatgravyboat.skyblockapi.mixins.accessors.AbstractContainerScreenAccessor

/**
 * This contains extensions of which accessors or interfaces are injected on vanilla classes.
 */


public fun AbstractContainerScreen<*>.getHoveredSlot(): Slot? = (this as AbstractContainerScreenAccessor).hoveredSlot

public val AbstractContainerScreen<*>.left: Int get() = (this as AbstractContainerScreenAccessor).leftPos
public val AbstractContainerScreen<*>.top: Int get() = (this as AbstractContainerScreenAccessor).topPos

public val AbstractContainerScreen<*>.containerWidth: Int get() = (this as AbstractContainerScreenAccessor).containerWidth
public val AbstractContainerScreen<*>.containerHeight: Int get() = (this as AbstractContainerScreenAccessor).containerHeight

public val AbstractContainerScreen<*>.right: Int get() = left + containerWidth
public val AbstractContainerScreen<*>.bottom: Int get() = top + containerHeight
