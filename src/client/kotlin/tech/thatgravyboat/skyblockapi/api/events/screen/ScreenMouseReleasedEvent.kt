package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.api.events.base.SkyblockEvent

open class ScreenMouseReleasedEvent(
    val screen: Screen,
    val x: Double,
    val y: Double,
    val button: Int,
) : SkyblockEvent() {

    class Pre(screen: Screen, x: Double, y: Double, button: Int) : ScreenMouseReleasedEvent(screen, x, y, button)
    class Post(screen: Screen, x: Double, y: Double, button: Int) : ScreenMouseReleasedEvent(screen, x, y, button)
}
