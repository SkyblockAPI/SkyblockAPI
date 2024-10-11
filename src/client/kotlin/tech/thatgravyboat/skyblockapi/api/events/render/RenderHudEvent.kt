package tech.thatgravyboat.skyblockapi.api.events.render

import net.minecraft.client.gui.GuiGraphics
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent

data class RenderHudEvent(
    val graphics: GuiGraphics,
    val partialTicks: Float,
) : CancellableSkyBlockEvent()
