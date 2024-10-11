package tech.thatgravyboat.skyblockapi.api.events.render

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

open class RenderScreenEvent(val screen: Screen) : SkyBlockEvent()

class RenderScreenForegroundEvent(screen: Screen, val graphics: GuiGraphics) : RenderScreenEvent(screen)

class RenderScreenBackgroundEvent(screen: Screen, val graphics: GuiGraphics) : RenderScreenEvent(screen)
