package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent

public sealed class ScreenKeyReleasedEvent(
    public val screen: Screen,
    public val key: Int,
    //? < 26.3
    //val scanCode: Int,
    public val modifiers: Int,
) : CancellableSkyBlockEvent() {

    public class Pre(
        screen: Screen,
        key: Int,
        //? < 26.3
        //scanCode: Int,
        modifiers: Int,
    ) : ScreenKeyReleasedEvent(
        screen,
        key,
        //? < 26.3
        //scanCode,
        modifiers,
    )

    public class Post(
        screen: Screen,
        key: Int,
        //? < 26.3
        //scanCode: Int,
        modifiers: Int,
    ) : ScreenKeyReleasedEvent(
        screen,
        key,
        //? < 26.3
        //scanCode,
        modifiers,
    )
}
