@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package tech.thatgravyboat.skyblockapi.helpers

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen

actual object McScreen {

    actual val self: Screen? get() = Minecraft.getInstance().screen

    actual val asMenu: AbstractContainerScreen<*>? get() = self as? AbstractContainerScreen<*>

    actual val isShiftDown get() = Minecraft.getInstance().hasShiftDown()
    actual val isAltDown get() = Minecraft.getInstance().hasAltDown()
    actual val isControlDown get() = Minecraft.getInstance().hasControlDown()

    actual inline fun <reified T> isOf(): Boolean {
        return self is T
    }
}
