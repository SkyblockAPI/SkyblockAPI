package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

open class ScreenMouseClickEvent(
    val screen: Screen,
    val x: Double,
    val y: Double,
    val button: Int,
) : SkyBlockEvent() {

    class Pre(screen: Screen, x: Double, y: Double, button: Int) : ScreenMouseClickEvent(screen, x, y, button)
    class Post(screen: Screen, x: Double, y: Double, button: Int) : ScreenMouseClickEvent(screen, x, y, button)
}
