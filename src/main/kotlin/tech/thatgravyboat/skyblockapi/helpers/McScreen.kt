package tech.thatgravyboat.skyblockapi.helpers

import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen

public object McScreen {

    public val self: Screen? get() =/*? if >= 26.2 {*/ McClient.gui.screen() /*? } else*///McClient.self.screen

    public val asMenu: AbstractContainerScreen<*>? get() = self as? AbstractContainerScreen<*>

    public val isShiftDown
        get() = McClient.self.hasShiftDown()

    public val isAltDown
        get() = McClient.self.hasAltDown()

    public val isControlDown
        get() = McClient.self.hasControlDown()

    public inline fun <reified T> isOf(): Boolean {
        return self is T
    }
}
