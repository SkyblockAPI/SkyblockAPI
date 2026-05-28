package tech.thatgravyboat.skyblockapi.helpers

import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen

object McScreen {

    val self: Screen? get() =/*? if >= 26.2 {*/ McClient.gui.screen() /*? } else*///McClient.self.screen

    val asMenu: AbstractContainerScreen<*>? get() = self as? AbstractContainerScreen<*>

    val isShiftDown
        get() = McClient.self.hasShiftDown()

    val isAltDown
        get() = McClient.self.hasAltDown()

    val isControlDown
        get() = McClient.self.hasControlDown()

    inline fun <reified T> isOf(): Boolean {
        return self is T
    }
}
