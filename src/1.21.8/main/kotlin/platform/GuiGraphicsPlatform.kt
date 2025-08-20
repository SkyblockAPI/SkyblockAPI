package tech.thatgravyboat.skyblockapi.platform

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.gui.render.state.BlitRenderState
import net.minecraft.client.gui.render.state.GuiElementRenderState
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FormattedText
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ARGB
import net.minecraft.util.FormattedCharSequence
import net.minecraft.util.Mth
import org.joml.Matrix3x2f
import org.joml.Vector2f
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont

// in 1.21.5 this was done in Font, in 1.21.7 this is no longer the case
private inline fun adjustColor(color: Int): Int {
    return if ((color and 0xfc000000.toInt()) == 0) ARGB.opaque(color) else color
}

private class GradientGuiElement(
    val pose: Matrix3x2f,
    val x0: Int, val y0: Int, val x1: Int, val y1: Int, val col1: Int, val col2: Int, val col3: Int, val col4: Int,
    val scissor: ScreenRectangle? = null,
    val bounds: ScreenRectangle? = null,
) : GuiElementRenderState {

    override fun buildVertices(consumer: VertexConsumer, z: Float) {
        consumer.addVertexWith2DPose(this.pose, this.x0.toFloat(), this.y0.toFloat(), z).setColor(this.col1)
        consumer.addVertexWith2DPose(this.pose, this.x0.toFloat(), this.y1.toFloat(), z).setColor(this.col2)
        consumer.addVertexWith2DPose(this.pose, this.x1.toFloat(), this.y1.toFloat(), z).setColor(this.col3)
        consumer.addVertexWith2DPose(this.pose, this.x1.toFloat(), this.y0.toFloat(), z).setColor(this.col4)
    }

    override fun pipeline(): RenderPipeline = RenderPipelines.GUI
    override fun textureSetup(): TextureSetup = TextureSetup.noTexture()
    override fun scissorArea(): ScreenRectangle? = scissor
    override fun bounds(): ScreenRectangle? = bounds
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

actual fun GuiGraphics.rotate(angle: Number, x: Number, y: Number) {
    if (x.toFloat() == 0f && y.toFloat() == 0f) {
        this.pose().rotate(angle.toFloat() * Mth.DEG_TO_RAD)
    } else {
        this.pose().rotateAbout(angle.toFloat() * Mth.DEG_TO_RAD, x.toFloat(), y.toFloat())
    }
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

actual fun GuiGraphics.drawGradient(
    x: Int, y: Int, width: Int, height: Int,
    col1: Int, col2: Int,
    col3: Int, col4: Int,
) {
    val scissor = this.scissorStack.peek()
    val pose = Matrix3x2f(this.pose())
    this.guiRenderState.submitGuiElement(GradientGuiElement(
        pose,
        x, y, x + width, y + height,
        col1, col2, col3, col4,
        scissor,
        ScreenRectangle(x, y, width, height).transformMaxBounds(pose).let { bounds -> scissor?.intersection(bounds) ?: bounds },
    ))
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
