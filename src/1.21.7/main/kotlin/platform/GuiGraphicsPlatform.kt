package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.gui.render.state.BlitRenderState
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.msrandom.stub.Stub
import org.joml.Matrix3x2f
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont

actual fun GuiGraphics.drawString(text: String, x: Int, y: Int, color: Int, shadow: Boolean) {
    this.drawString(McFont.self, text, x, y, color, shadow)
}

actual fun GuiGraphics.drawString(text: Component, x: Int, y: Int, color: Int, shadow: Boolean) {
    this.drawString(McFont.self, text, x, y, color, shadow)
}

actual fun GuiGraphics.drawSprite(texture: ResourceLocation, x: Int, y: Int, width: Int, height: Int, color: Int) {
    this.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x, y, width, height, color)
}

actual fun GuiGraphics.drawTexture(texture: ResourceLocation, x: Int, y: Int, width: Int, height: Int, u0: Float, v0: Float, u1: Float, v1: Float, color: Int) {
    val minx = x
    val miny = y
    val maxx = (x + width)
    val maxy = (y + height)

    this.guiRenderState.submitGuiElement(
        BlitRenderState(
            RenderPipelines.GUI_TEXTURED, TextureSetup.singleTexture(McClient.self.textureManager.getTexture(texture).textureView), Matrix3x2f(this.pose()),
            minx, miny, maxx, maxy, u0, v0, u1, v1, color,
            this.scissorStack.peek(),
        ),
    )
}

actual fun GuiGraphics.showTooltip(text: Component, x: Int, y: Int) {
    this.setTooltipForNextFrame(Tooltip.splitTooltip(McClient.self, text), x, y)
}
