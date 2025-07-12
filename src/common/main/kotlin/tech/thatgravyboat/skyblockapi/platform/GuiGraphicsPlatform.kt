package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.msrandom.stub.Stub

@Stub expect fun GuiGraphics.drawString(text: String, x: Int, y: Int, color: Int = 0xFFFFFFFF.toInt(), shadow: Boolean = false)
@Stub expect fun GuiGraphics.drawString(text: Component, x: Int, y: Int, color: Int = 0xFFFFFFFF.toInt(), shadow: Boolean = false)
