package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner
import net.minecraft.client.renderer.RenderType
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FormattedText
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.FormattedCharSequence
import org.joml.Vector2f
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McScreen

actual inline fun GuiGraphics.pushPop(block: () -> Unit) {
    this.pose().pushPose()
    block()
    this.pose().popPose()
}

actual fun GuiGraphics.translate(x: Number, y: Number) = this.pose().translate(x.toFloat(), y.toFloat(), 0f)
actual fun GuiGraphics.scale(x: Number, y: Number) = this.pose().scale(x.toFloat(), y.toFloat(), 1f)

actual fun GuiGraphics.drawString(text: String, x: Int, y: Int, color: Int, shadow: Boolean) {
    this.drawString(McFont.self, text, x, y, color, shadow)
}

actual fun GuiGraphics.drawString(text: FormattedText, x: Int, y: Int, color: Int, shadow: Boolean) {
    this.drawString(McFont.self, Language.getInstance().getVisualOrder(text), x, y, color, shadow)
}

actual fun GuiGraphics.drawString(text: FormattedCharSequence, x: Int, y: Int, color: Int, shadow: Boolean) {
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

actual fun GuiGraphics.showTooltip(text: Component) {
    val screen = McScreen.self ?: return
    screen.setTooltipForNextRenderPass(
        Tooltip.splitTooltip(McClient.self, text),
        DefaultTooltipPositioner.INSTANCE,
        true
    )
}

actual fun GuiGraphics.showTooltip(text: Component, x: Int, y: Int) {
    val screen = McScreen.self ?: return
    screen.setTooltipForNextRenderPass(
        Tooltip.splitTooltip(McClient.self, text),
        ClientTooltipPositioner { screenWidth, screenHeight, _, _, width, height ->
            DefaultTooltipPositioner.INSTANCE.positionTooltip(screenWidth, screenHeight, x, y, width, height)
        },
        true
    )
}

actual fun GuiGraphics.getTranslation(): Vector2f = Vector2f(this.pose().last().pose().m30(), this.pose().last().pose().m31())
actual fun GuiGraphics.getScale(): Vector2f = Vector2f(this.pose().last().pose().m00(), this.pose().last().pose().m11())

actual fun GuiGraphics.applyBackgroundBlur() {
    McClient.self.gameRenderer.processBlurEffect()
}

