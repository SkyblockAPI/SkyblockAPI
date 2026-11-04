package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent

public sealed class ScreenKeyPressedEvent(
    public val screen: Screen,
    public val event: KeyEvent,
) : CancellableSkyBlockEvent() {
    //? < 26.4 {
    /*protected constructor(
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
    )*///?}

    public val key: Int = event.key

    //~ if > 26.2 'scancode' -> 'keycode'
    public val keycode: Int = event.keycode
    public val modifiers: Int = event.modifiers

    //? < 26.3
    //public val scanCode: Int = event.scancode

    public class Pre(screen: Screen, event: KeyEvent) : ScreenKeyPressedEvent(screen, event)
    //? < 26.4 {
    /*{
        @Deprecated("Provide a KeyEvent instead", level = DeprecationLevel.HIDDEN)
        public constructor(
            screen: Screen,
            key: Int,
            //? < 26.3
            //scanCode: Int,
            modifiers: Int,
        ) : this(
            screen,
            //~ if >= 26.3 'scanCode' -> '0'
            KeyEvent(key, 0, modifiers),
        )
    }*///?}

    /**
     * Only used on 26.4+ for slot navigation using keys,
     * will not be fired on 26.3 and below.
     */
    public class Navigation internal constructor(screen: Screen, event: KeyEvent) : ScreenKeyPressedEvent(screen, event)

    public class Post(screen: Screen, event: KeyEvent) : ScreenKeyPressedEvent(screen, event)
    //? < 26.4 {
    /*{
        @Deprecated("Provide a KeyEvent instead", level = DeprecationLevel.HIDDEN)
        public constructor(
            screen: Screen,
            key: Int,
            //? < 26.3
            //scanCode: Int,
            modifiers: Int,
        ) : this(
            screen,
            //~ if >= 26.3 'scanCode' -> '0'
            KeyEvent(key, 0, modifiers),
        )
    }*///?}
}
