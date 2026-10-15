package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyPressedEvent

public sealed class ScreenKeyReleasedEvent(
    public val screen: Screen,
    public val event: KeyEvent,
) : CancellableSkyBlockEvent() {
    protected constructor(
        screen: Screen,
        key: Int,
        //? < 26.3
        //scanCode: Int,
        modifiers: Int,
    ) : this(screen, KeyEvent(key,
        //? < 26.3
        //scanCode,
        //? else
        0,
        modifiers)
    )
    public val key: Int = event.key

    //~ if > 26.2 'scancode' -> 'keycode'
    public val keycode: Int = event.keycode
    public val modifiers: Int = event.modifiers

    //? < 26.3
    //public val scanCode: Int = event.scancode

    public class Pre(screen: Screen, event: KeyEvent) : ScreenKeyReleasedEvent(screen, event)
    //? < 26.4 {
    {
        @Deprecated("Provide a KeyEvent instead", level = DeprecationLevel.HIDDEN)
        public constructor(
            screen: Screen,
            key: Int,
            //? < 26.3
            //scanCode: Int,
            modifiers: Int,
        ) : this(
            screen,
            //~ if < 26.3 'scanCode' -> '0'
            KeyEvent(key, 0, modifiers),
        )
    }//?}

    public class Post(screen: Screen, event: KeyEvent) : ScreenKeyReleasedEvent(screen, event)
    //? < 26.4 {
    {
        @Deprecated("Provide a KeyEvent instead", level = DeprecationLevel.HIDDEN)
        public constructor(
            screen: Screen,
            key: Int,
            //? < 26.3
            //scanCode: Int,
            modifiers: Int,
        ) : this(
            screen,
            //~ if < 26.3 'scanCode' -> '0'
            KeyEvent(key, 0, modifiers),
        )
    }//?}
}
