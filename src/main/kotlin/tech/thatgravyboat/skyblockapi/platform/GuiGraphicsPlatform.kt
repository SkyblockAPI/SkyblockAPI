package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.renderer.state.gui.BlitRenderState
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FormattedText
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import net.minecraft.util.FormattedCharSequence
import net.minecraft.util.Mth
import org.joml.Matrix3x2f
import org.joml.Vector2f
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont

@Suppress("NOTHING_TO_INLINE")
private inline fun adjustColor(color: Int): Int {
    return if ((color and 0xfc000000.toInt()) == 0) ARGB.opaque(color) else color
}

inline fun GuiGraphicsExtractor.pushPop(block: () -> Unit) {
    this.pose().pushMatrix()
    block()
    this.pose().popMatrix()
}

fun GuiGraphicsExtractor.translate(x: Number, y: Number) {
    pose().translate(x.toFloat(), y.toFloat())
}

fun GuiGraphicsExtractor.scale(x: Number, y: Number) {
    pose().scale(x.toFloat(), y.toFloat())
}

fun GuiGraphicsExtractor.rotate(angle: Number, x: Number = 0f, y: Number = 0f) {
    if (x.toFloat() == 0f && y.toFloat() == 0f) {
        this.pose().rotate(angle.toFloat() * Mth.DEG_TO_RAD)
    } else {
        this.pose().rotateAbout(angle.toFloat() * Mth.DEG_TO_RAD, x.toFloat(), y.toFloat())
    }
}

fun GuiGraphicsExtractor.drawString(text: String, x: Int, y: Int, color: Int = -1, shadow: Boolean = false) {
    //~ if >= 26.1 'drawString(' -> 'text('
    this.text(McFont.self, text, x, y, adjustColor(color), shadow)
}

fun GuiGraphicsExtractor.drawString(text: FormattedText, x: Int, y: Int, color: Int = -1, shadow: Boolean = false) {
    //~ if >= 26.1 'drawString(' -> 'text('
    this.text(McFont.self, Language.getInstance().getVisualOrder(text), x, y, adjustColor(color), shadow)
}

fun GuiGraphicsExtractor.drawString(text: FormattedCharSequence, x: Int, y: Int, color: Int = -1, shadow: Boolean = false) {
    //~ if >= 26.1 'drawString(' -> 'text('
    this.text(McFont.self, text, x, y, adjustColor(color), shadow)
}


fun GuiGraphicsExtractor.drawSprite(texture: Identifier, x: Int, y: Int, width: Int, height: Int, color: Int = -1) {
    this.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x, y, width, height, color)
}

fun GuiGraphicsExtractor.drawTexture(
    texture: Identifier, x: Int, y: Int, width: Int, height: Int,
    u0: Float = 0f, v0: Float = 0f, u1: Float = 1f, v1: Float = 1f,
    color: Int = -1,
) {
    val minx = x
    val miny = y
    val maxx = (x + width)
    val maxy = (y + height)

    val setup: TextureSetup
    //? if >= 1.21.11 {
        = McClient.self.textureManager.getTexture(texture).let { TextureSetup.singleTexture(it.textureView, it.sampler) }
    //?} else {
    /*= TextureSetup.singleTexture(McClient.self.textureManager.getTexture(texture).textureView)
     *///?}

    //~ if >= 26.1 'submitGuiElement' -> 'addGuiElement'
    this.guiRenderState.addGuiElement(
        BlitRenderState(
            RenderPipelines.GUI_TEXTURED, setup, Matrix3x2f(this.pose()),
            minx, miny, maxx, maxy, u0, u1, v0, v1, color,
            this.scissorStack.peek(),
        ),
    )
}

fun GuiGraphicsExtractor.drawGradient(
    x: Int, y: Int, width: Int, height: Int,
    col1: Int, col2: Int, col3: Int, col4: Int,
) {
    val scissor = this.scissorStack.peek()
    val pose = Matrix3x2f(this.pose())
    //~ if >= 26.1 'submitGuiElement' -> 'addGuiElement'
    this.guiRenderState.addGuiElement(
        GradientGuiElement(
            pose,
            x, y, x + width, y + height,
            col1, col2, col3, col4,
            scissor,
            ScreenRectangle(x, y, width, height).transformMaxBounds(pose).let { bounds -> scissor?.intersection(bounds) ?: bounds },
        ),
    )
}

fun GuiGraphicsExtractor.drawFilledBox(x: Int, y: Int, width: Int, height: Int, color: Int = -1) {
    this.fill(x, y, x + width, y + height, color)
}

fun GuiGraphicsExtractor.drawOutline(x: Int, y: Int, width: Int, height: Int, color: Int = -1) {
    this.fill(x, y, x + width, y + 1, color)
    this.fill(x, y + height - 1, x + width, y + height, color)
    this.fill(x, y + 1, x + 1, y + height - 1, color)
    this.fill(x + width - 1, y + 1, x + width, y + height - 1, color)
}

fun GuiGraphicsExtractor.showTooltip(text: Component, maxWidth: Int = Int.MAX_VALUE, force: Boolean = true) {
    val (x, y) = McClient.mouse
    this.setTooltipForNextFrame(McFont.self, McFont.split(text, maxWidth), DefaultTooltipPositioner.INSTANCE, x.toInt(), y.toInt(), false)
}

fun GuiGraphicsExtractor.showTooltip(text: Component, x: Int, y: Int, maxWidth: Int = Int.MAX_VALUE, force: Boolean = true) {
    this.setTooltipForNextFrame(McFont.self, McFont.split(text, maxWidth), DefaultTooltipPositioner.INSTANCE, x, y, false)
}

fun GuiGraphicsExtractor.getTranslation(): Vector2f {
    return Vector2f(this.pose().m20(), this.pose().m21())
}

fun GuiGraphicsExtractor.getScale(): Vector2f {
    return Vector2f(this.pose().m00(), this.pose().m11())
}

fun GuiGraphicsExtractor.applyBackgroundBlur() {
    this.blurBeforeThisStratum()
}
