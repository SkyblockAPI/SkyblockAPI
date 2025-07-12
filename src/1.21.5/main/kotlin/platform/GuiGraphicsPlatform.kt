package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.helpers.McFont

actual fun GuiGraphics.drawString(text: String, x: Int, y: Int, color: Int, shadow: Boolean) {
    this.drawString(McFont.self, text, x, y, color, shadow)
}

actual fun GuiGraphics.drawString(text: Component, x: Int, y: Int, color: Int, shadow: Boolean) {
    this.drawString(McFont.self, text, x, y, color, shadow)
}


actual fun GuiGraphics.drawSprite(texture: ResourceLocation, x: Int, y: Int, width: Int, height: Int, color: Int) {
    this.blitSprite(RenderType::guiTextured, texture, x, y, width, height, color)
}
actual fun GuiGraphics.drawTexture(texture: ResourceLocation, x: Int, y: Int, width: Int, height: Int, u0: Float, v0: Float, u1: Float, v1: Float, color: Int) {
    val matrix = this.pose().last().pose()
    val minx = x.toFloat()
    val miny = y.toFloat()
    val maxx = (x + width).toFloat()
    val maxy = (y + height).toFloat()


    this.drawSpecial { source ->
        var buffer = source.getBuffer(RenderType.guiTextured(texture))
        buffer.addVertex(matrix, minx, miny, 0f).setColor(color).setUv(u0, v0)
        buffer.addVertex(matrix, minx, maxy, 0f).setColor(color).setUv(u0, v1)
        buffer.addVertex(matrix, maxx, maxy, 0f).setColor(color).setUv(u1, v1)
        buffer.addVertex(matrix, maxx, miny, 0f).setColor(color).setUv(u1, v0)
    }
}
