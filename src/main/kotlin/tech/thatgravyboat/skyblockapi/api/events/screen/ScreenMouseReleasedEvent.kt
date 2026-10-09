package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent

public sealed class ScreenMouseReleasedEvent(
    public val screen: Screen,
    public val x: Double,
    public val y: Double,
    public val button: Int,
) : CancellableSkyBlockEvent() {

    public class Pre(screen: Screen, x: Double, y: Double, button: Int) : ScreenMouseReleasedEvent(screen, x, y, button)
    public class Post(screen: Screen, x: Double, y: Double, button: Int) : ScreenMouseReleasedEvent(screen, x, y, button)
}
