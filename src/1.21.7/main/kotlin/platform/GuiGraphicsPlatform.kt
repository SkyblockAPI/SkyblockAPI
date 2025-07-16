package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.gui.render.state.BlitRenderState
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FormattedText
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ARGB
import net.minecraft.util.FormattedCharSequence
import org.joml.Matrix3x2f
import org.joml.Vector2f
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont

// in 1.21.5 this was done in Font, in 1.21.7 this is no longer the case
private inline fun adjustColor(color: Int): Int {
    return if ((color and 0xfc000000.toInt()) == 0) ARGB.opaque(color) else color
}

actual inline fun GuiGraphics.pushPop(block: () -> Unit) {
    this.pose().pushMatrix()
    block()
    this.pose().popMatrix()
}

actual fun GuiGraphics.translate(x: Number, y: Number) {
    this.pose().translate(x.toFloat(), y.toFloat())
}
actual fun GuiGraphics.scale(x: Number, y: Number) {
    this.pose().scale(x.toFloat(), y.toFloat())
}
actual fun GuiGraphics.rotate(angle: Number) {
    this.pose().rotate(angle.toFloat())
}

actual fun GuiGraphics.drawString(text: String, x: Int, y: Int, color: Int, shadow: Boolean) {
    this.drawString(McFont.self, text, x, y, adjustColor(color), shadow)
}

actual fun GuiGraphics.drawString(text: FormattedText, x: Int, y: Int, color: Int, shadow: Boolean) {
    this.drawString(McFont.self, Language.getInstance().getVisualOrder(text), x, y, adjustColor(color), shadow)
}

actual fun GuiGraphics.drawString(text: FormattedCharSequence, x: Int, y: Int, color: Int, shadow: Boolean) {
    this.drawString(McFont.self, text, x, y, adjustColor(color), shadow)
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
            minx, miny, maxx, maxy, u0, u1, v0, v1, color,
            this.scissorStack.peek(),
        ),
    )
}

actual fun GuiGraphics.showTooltip(text: Component, maxWidth: Int, force: Boolean) {
    val (x, y) = McClient.mouse
    this.setTooltipForNextFrame(McFont.self, McFont.split(text, maxWidth), DefaultTooltipPositioner.INSTANCE, x.toInt(), y.toInt(), false)
}

actual fun GuiGraphics.showTooltip(text: Component, x: Int, y: Int, maxWidth: Int, force: Boolean) {
    this.setTooltipForNextFrame(McFont.self, McFont.split(text, maxWidth), DefaultTooltipPositioner.INSTANCE, x.toInt(), y.toInt(), false)
}


actual fun GuiGraphics.getTranslation(): Vector2f = Vector2f(this.pose().m20(), this.pose().m21())
actual fun GuiGraphics.getScale(): Vector2f = Vector2f(this.pose().m00(), this.pose().m11())

actual fun GuiGraphics.applyBackgroundBlur() {
    this.blurBeforeThisStratum()
}
