package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.api.events.base.SkyblockEvent

open class ScreenKeyReleasedEvent(
    val screen: Screen,
    val key: Int,
    val scanCode: Int,
    val modifiers: Int,
) : SkyblockEvent() {

    class Pre(screen: Screen, key: Int, scanCode: Int, modifiers: Int) : ScreenKeyReleasedEvent(screen, key, scanCode, modifiers)
    class Post(screen: Screen, key: Int, scanCode: Int, modifiers: Int) : ScreenKeyReleasedEvent(screen, key, scanCode, modifiers)
}
