package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.Identifier
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FormattedText
import net.minecraft.util.ARGB
import net.minecraft.util.FormattedCharSequence
import net.minecraft.util.Mth
import org.joml.Matrix3x2f
import org.joml.Vector2f
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont

//? if > 1.21.5 {
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.gui.render.state.BlitRenderState
//?} else {
/*import tech.thatgravyboat.skyblockapi.helpers.McScreen
import net.minecraft.client.renderer.RenderType
*///?}

@Suppress("NOTHING_TO_INLINE")
private inline fun adjustColor(color: Int): Int {

    // in 1.21.5 this was done in Font, in 1.21.7 this is no longer the case
    //? if > 1.21.5 {
    return if ((color and 0xfc000000.toInt()) == 0) ARGB.opaque(color) else color
    //?} else
    /*return color*/
}

inline fun GuiGraphics.pushPop(block: () -> Unit) {
    //? if > 1.21.5 {
    this.pose().pushMatrix()
    //?} else
    /*this.pose().pushPose()*/
    block()
    //? if > 1.21.5 {
    this.pose().popMatrix()
    //?} else
    /*this.pose().popPose()*/
}

fun GuiGraphics.translate(x: Number, y: Number) {
    pose().translate(x.toFloat(), y.toFloat()/*? if <= 1.21.5 {*//*, 0f *//*?}*/)
}

fun GuiGraphics.scale(x: Number, y: Number) {
    pose().scale(x.toFloat(), y.toFloat()/*? if <= 1.21.5 {*//*, 1f *//*?}*/)
}

fun GuiGraphics.rotate(angle: Number, x: Number = 0f, y: Number = 0f) {
    //? if > 1.21.5 {
    if (x.toFloat() == 0f && y.toFloat() == 0f) {
        this.pose().rotate(angle.toFloat() * Mth.DEG_TO_RAD)
    } else {
        this.pose().rotateAbout(angle.toFloat() * Mth.DEG_TO_RAD, x.toFloat(), y.toFloat())
    }
    //?} else
    /*pose().rotateAround(com.mojang.math.Axis.ZP.rotationDegrees(angle.toFloat()), x.toFloat(), y.toFloat(), 0f)*/
}

fun GuiGraphics.drawString(text: String, x: Int, y: Int, color: Int = -1, shadow: Boolean = false) {
    this.drawString(McFont.self, text, x, y, adjustColor(color), shadow)
}

fun GuiGraphics.drawString(text: FormattedText, x: Int, y: Int, color: Int = -1, shadow: Boolean = false) {
    this.drawString(McFont.self, Language.getInstance().getVisualOrder(text), x, y, adjustColor(color), shadow)
}

fun GuiGraphics.drawString(text: FormattedCharSequence, x: Int, y: Int, color: Int = -1, shadow: Boolean = false) {
    this.drawString(McFont.self, text, x, y, adjustColor(color), shadow)
}


fun GuiGraphics.drawSprite(texture: Identifier, x: Int, y: Int, width: Int, height: Int, color: Int = -1) {
    //? if > 1.21.5 {
    this.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x, y, width, height, color)
    //?} else
    /*this.blitSprite(RenderType::guiTextured, texture, x, y, width, height, color)*/
}
fun GuiGraphics.drawTexture(
    texture: Identifier, x: Int, y: Int, width: Int, height: Int,
    u0: Float = 0f, v0: Float = 0f, u1: Float = 1f, v1: Float = 1f,
    color: Int = -1,
) {
    //? if > 1.21.5 {
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

    this.guiRenderState.submitGuiElement(
        BlitRenderState(
            RenderPipelines.GUI_TEXTURED, setup, Matrix3x2f(this.pose()),
            minx, miny, maxx, maxy, u0, u1, v0, v1, color,
            this.scissorStack.peek(),
        ),
    )
    //?} else {
    /*val matrix = this.pose().last().pose()
    val minx = x.toFloat()
    val miny = y.toFloat()
    val maxx = (x + width).toFloat()
    val maxy = (y + height).toFloat()


    this.drawSpecial { source ->
        val buffer = source.getBuffer(RenderType.guiTextured(texture))
        buffer.addVertex(matrix, minx, miny, 0f).setColor(color).setUv(u0, v0)
        buffer.addVertex(matrix, minx, maxy, 0f).setColor(color).setUv(u0, v1)
        buffer.addVertex(matrix, maxx, maxy, 0f).setColor(color).setUv(u1, v1)
        buffer.addVertex(matrix, maxx, miny, 0f).setColor(color).setUv(u1, v0)
    }
    *///?}
}

//? if = 1.21.5 {
/*@Deprecated("", replaceWith = ReplaceWith("drawGradientBox"))
fun GuiGraphics.drawGradient(
    x: Int, y: Int, width: Int, height: Int,
    col1: Int, col2: Int, col3: Int, col4: Int,
) = drawGradientBox(x, y, width, height, col1, col2, col3, col4)
*///?}

fun GuiGraphics.drawGradientBox(
    x: Int, y: Int, width: Int, height: Int,
    col1: Int, col2: Int, col3: Int, col4: Int,
) {
    //? if > 1.21.5 {
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
    //?} else {
    /*val matrix = this.pose().last().pose()
    val minx = x.toFloat()
    val miny = y.toFloat()
    val maxx = (x + width).toFloat()
    val maxy = (y + height).toFloat()


    this.drawSpecial { source ->
        val buffer = source.getBuffer(RenderType.gui())
        buffer.addVertex(matrix, minx, miny, 0f).setColor(col1)
        buffer.addVertex(matrix, minx, maxy, 0f).setColor(col2)
        buffer.addVertex(matrix, maxx, maxy, 0f).setColor(col3)
        buffer.addVertex(matrix, maxx, miny, 0f).setColor(col4)
    }
    *///?}
}

fun GuiGraphics.drawFilledBox(x: Int, y: Int, width: Int, height: Int, color: Int = -1) {
    this.fill(x, y, x + width, y + height, color)
}

fun GuiGraphics.drawOutline(x: Int, y: Int, width: Int, height: Int, color: Int = -1) {
    // I don't know what minecraft cooked here, they reverted this change in 1.21.11
    //? if > 1.21.8 {
    this.fill(x, y, x + width, y + 1, color)
    this.fill(x, y + height - 1, x + width, y + height, color)
    this.fill(x, y + 1, x + 1, y + height - 1, color)
    this.fill(x + width - 1, y + 1, x + width, y + height - 1, color)
    //?} else {
    /*this.renderOutline(x, y, width, height, color)
    *///?}
}

fun GuiGraphics.showTooltip(text: Component, maxWidth: Int = Int.MAX_VALUE, force: Boolean = true) {
    //? if > 1.21.5 {
    val (x, y) = McClient.mouse
    this.setTooltipForNextFrame(McFont.self, McFont.split(text, maxWidth), DefaultTooltipPositioner.INSTANCE, x.toInt(), y.toInt(), false)
    //?} else {
    /*val screen = McScreen.self ?: return
    screen.setTooltipForNextRenderPass(
        McFont.split(text, maxWidth),
        DefaultTooltipPositioner.INSTANCE,
        force,
    )
    *///?}
}
fun GuiGraphics.showTooltip(text: Component, x: Int, y: Int, maxWidth: Int = Int.MAX_VALUE, force: Boolean = true) {
    //? if > 1.21.5 {
    this.setTooltipForNextFrame(McFont.self, McFont.split(text, maxWidth), DefaultTooltipPositioner.INSTANCE, x.toInt(), y.toInt(), false)
    //?} else {
    /*val screen = McScreen.self ?: return
    screen.setTooltipForNextRenderPass(
        McFont.split(text, maxWidth),
        { screenWidth, screenHeight, _, _, width, height ->
            DefaultTooltipPositioner.INSTANCE.positionTooltip(screenWidth, screenHeight, x, y, width, height)
        },
        force,
    )
    *///?}
}

fun GuiGraphics.getTranslation(): Vector2f {
    //? if > 1.21.5 {
    return Vector2f(this.pose().m20(), this.pose().m21())
    //?} else
    /*return Vector2f(this.pose().last().pose().m30(), this.pose().last().pose().m31())*/
}
fun GuiGraphics.getScale(): Vector2f {
    //? if > 1.21.5 {
    return Vector2f(this.pose().m00(), this.pose().m11())
    //?} else
    /*return Vector2f(this.pose().last().pose().m00(), this.pose().last().pose().m11())*/
}

fun GuiGraphics.applyBackgroundBlur() {
    //? if > 1.21.5 {
    this.blurBeforeThisStratum()
    //?} else
    /*McClient.self.gameRenderer.processBlurEffect()*/
}
