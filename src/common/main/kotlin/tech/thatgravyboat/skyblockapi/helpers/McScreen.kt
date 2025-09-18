package tech.thatgravyboat.skyblockapi.helpers

import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen

expect object McScreen {

    val self: Screen?

    val asMenu: AbstractContainerScreen<*>?

    val isShiftDown: Boolean
    val isAltDown: Boolean
    val isControlDown: Boolean

    inline fun <reified T> isOf(): Boolean
}
