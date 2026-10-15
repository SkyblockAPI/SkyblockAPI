package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent

public sealed class ScreenKeyPressedEvent(
    public val screen: Screen,
    public val event: KeyEvent,
) : CancellableSkyBlockEvent() {
    public val key: Int = event.key

    //~ if > 26.2 'scancode' -> 'keycode'
    public val keycode: Int = event.keycode
    public val modifiers: Int = event.modifiers

    //? < 26.3
    //public val scanCode: Int = event.scancode

    public class Pre(screen: Screen, event: KeyEvent) : ScreenKeyPressedEvent(screen, event)
    public class Post(screen: Screen, event: KeyEvent) : ScreenKeyPressedEvent(screen, event)
}
