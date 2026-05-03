package tech.thatgravyboat.skyblockapi.api.events.render

import net.minecraft.client.gui.GuiGraphicsExtractor
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent

data class RenderHudEvent(
    val graphics: GuiGraphicsExtractor,
    val partialTicks: Float,
) : CancellableSkyBlockEvent()
