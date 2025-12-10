package tech.thatgravyboat.skyblockapi.helpers

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen

object McScreen {

    val self: Screen? get() = Minecraft.getInstance().screen

    val asMenu: AbstractContainerScreen<*>? get() = self as? AbstractContainerScreen<*>

    val isShiftDown
    //? if > 1.21.8 {
        get() = McClient.self.hasShiftDown()
    //?} else
        /*get() = Screen.hasShiftDown()*/

    val isAltDown
    //? if > 1.21.8 {
        get() = McClient.self.hasAltDown()
    //?} else
        /*get() = Screen.hasAltDown()*/

    val isControlDown
    //? if > 1.21.8 {
        get() = McClient.self.hasControlDown()
    //?} else
        /*get() = Screen.hasControlDown()*/

    inline fun <reified T> isOf(): Boolean {
        return self is T
    }
}
