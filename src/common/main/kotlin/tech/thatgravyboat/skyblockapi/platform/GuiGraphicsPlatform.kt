package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.msrandom.stub.Stub

@Stub expect fun GuiGraphics.drawString(text: String, x: Int, y: Int, color: Int = -1, shadow: Boolean = false)
@Stub expect fun GuiGraphics.drawString(text: Component, x: Int, y: Int, color: Int = -1, shadow: Boolean = false)

@Stub expect fun GuiGraphics.drawSprite(texture: ResourceLocation, x: Int, y: Int, width: Int, height: Int, color: Int = -1)
@Stub expect fun GuiGraphics.drawTexture(
    texture: ResourceLocation, x: Int, y: Int, width: Int, height: Int,
    u0: Float = 0f, v0: Float = 0f, u1: Float = 1f, v1: Float = 1f,
    color: Int = -1
)
