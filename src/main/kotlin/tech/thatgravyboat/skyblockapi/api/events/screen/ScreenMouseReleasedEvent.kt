package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent

//? < 26.4
import net.minecraft.client.input.MouseButtonInfo

public sealed class ScreenMouseReleasedEvent(
    public val screen: Screen,
    public val event: MouseButtonEvent,
) : CancellableSkyBlockEvent() {
    public val x: Double = event.x()
    public val y: Double = event.y()
    public val button: Int = event.button()

    public class Pre(screen: Screen, event: MouseButtonEvent) : ScreenMouseReleasedEvent(screen, event)
    //? < 26.4 {
    {
        @Deprecated("Provide a MouseButtonEvent instead", level = DeprecationLevel.HIDDEN)
        public constructor(screen: Screen, x: Double, y: Double, button: Int) : this(screen, MouseButtonEvent(x, y, MouseButtonInfo(button, 0)))
    }//?}

    public class Post(screen: Screen, event: MouseButtonEvent) : ScreenMouseReleasedEvent(screen, event)
    //? < 26.4 {
    {
        @Deprecated("Provide a MouseButtonEvent instead", level = DeprecationLevel.HIDDEN)
        public constructor(screen: Screen, x: Double, y: Double, button: Int) : this(screen, MouseButtonEvent(x, y, MouseButtonInfo(button, 0)))
    }//?}
}
