package tech.thatgravyboat.skyblockapi.api.events.render

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

public open class RenderScreenEvent(val screen: Screen) : SkyBlockEvent()

public class RenderScreenForegroundEvent(screen: Screen, val graphics: GuiGraphicsExtractor) : RenderScreenEvent(screen)

public class RenderScreenBackgroundEvent(screen: Screen, val graphics: GuiGraphicsExtractor) : RenderScreenEvent(screen), SkyBlockEvent.Cancellable
