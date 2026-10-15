package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent

public sealed class ScreenKeyReleasedEvent(
    public val screen: Screen,
    public val event: KeyEvent,
    //? < 26.3
    //public val scanCode: Int,
) : CancellableSkyBlockEvent() {
    public val key: Int = event.key
    public val keycode: Int = event.keycode
    public val modifiers: Int = event.modifiers

    //? < 26.3
    //public val scanCode: Int = event.scanCode

    public class Pre(screen: Screen, event: KeyEvent) : ScreenKeyReleasedEvent(screen, event)
    public class Post(screen: Screen, event: KeyEvent) : ScreenKeyReleasedEvent(screen, event)
}
