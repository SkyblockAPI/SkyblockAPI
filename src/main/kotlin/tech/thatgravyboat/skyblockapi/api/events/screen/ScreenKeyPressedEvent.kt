package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent

sealed class ScreenKeyPressedEvent(
    val screen: Screen,
    val key: Int,
    //? < 26.3
    //val scanCode: Int,
    val modifiers: Int,
) : CancellableSkyBlockEvent() {

    class Pre(
        screen: Screen,
        key: Int,
        //? < 26.3
        //scanCode: Int,
        modifiers: Int,
    ) : ScreenKeyPressedEvent(
        screen,
        key,
        //? < 26.3
        //scanCode,
        modifiers,
    )

    class Post(
        screen: Screen,
        key: Int,
        //? < 26.3
        //scanCode: Int,
        modifiers: Int,
    ) : ScreenKeyPressedEvent(
        screen,
        key,
        //? < 26.3
        //scanCode,
        modifiers,
    )
}
