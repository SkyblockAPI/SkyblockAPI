package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.MouseButtonEvent
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent

public sealed class ScreenMouseReleasedEvent(
    public val screen: Screen,
    public val event: MouseButtonEvent,
) : CancellableSkyBlockEvent() {
    public val x: Double = event.x()
    public val y: Double = event.y()
    public val button: Int = event.button()

    public class Pre(screen: Screen, event: MouseButtonEvent) : ScreenMouseReleasedEvent(screen, event)
    public class Post(screen: Screen, event: MouseButtonEvent) : ScreenMouseReleasedEvent(screen, event)
}
