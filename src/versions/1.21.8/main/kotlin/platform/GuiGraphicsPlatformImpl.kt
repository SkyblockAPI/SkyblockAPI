@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package tech.thatgravyboat.skyblockapi.platform

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.gui.render.state.GuiElementRenderState
import net.minecraft.client.renderer.RenderPipelines
import org.joml.Matrix3x2f

actual fun GuiGraphics.drawGradient(
    x: Int, y: Int, width: Int, height: Int,
    col1: Int, col2: Int,
    col3: Int, col4: Int,
) {
    val scissor = this.scissorStack.peek()
    val pose = Matrix3x2f(this.pose())
    this.guiRenderState.submitGuiElement(
        GradientGuiElement(
            pose,
            x, y, x + width, y + height,
            col1, col2, col3, col4,
            scissor,
            ScreenRectangle(x, y, width, height).transformMaxBounds(pose).let { bounds -> scissor?.intersection(bounds) ?: bounds },
        ),
    )
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

actual fun GuiGraphics.drawFilledBox(x: Int, y: Int, width: Int, height: Int, color: Int) {
    this.fill(x, y, x + width, y + height, color)
}

actual fun GuiGraphics.drawOutline(x: Int, y: Int, width: Int, height: Int, color: Int) {
    this.renderOutline(x, y, x + width, y + height, color)
}
