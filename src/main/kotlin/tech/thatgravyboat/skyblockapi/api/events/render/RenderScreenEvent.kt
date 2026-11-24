package tech.thatgravyboat.skyblockapi.api.events.render

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

public open class RenderScreenEvent(public val screen: Screen) : SkyBlockEvent()

public class RenderScreenForegroundEvent(screen: Screen, public val graphics: GuiGraphicsExtractor) : RenderScreenEvent(screen)

public class RenderScreenBackgroundEvent(screen: Screen, public val graphics: GuiGraphicsExtractor) : RenderScreenEvent(screen), SkyBlockEvent.Cancellable
