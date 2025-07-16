package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FormattedText
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.FormattedCharSequence
import net.msrandom.stub.Stub
import org.joml.Vector2f

@Stub expect inline fun GuiGraphics.pushPop(block: () -> Unit)
@Stub expect fun GuiGraphics.translate(x: Number, y: Number)
@Stub expect fun GuiGraphics.scale(x: Number, y: Number)
@Stub expect fun GuiGraphics.rotate(angle: Number)

@Stub expect fun GuiGraphics.drawString(text: String, x: Int, y: Int, color: Int = -1, shadow: Boolean = false)
@Stub expect fun GuiGraphics.drawString(text: FormattedText, x: Int, y: Int, color: Int = -1, shadow: Boolean = false)
@Stub expect fun GuiGraphics.drawString(text: FormattedCharSequence, x: Int, y: Int, color: Int = -1, shadow: Boolean = false)

@Stub expect fun GuiGraphics.drawSprite(texture: ResourceLocation, x: Int, y: Int, width: Int, height: Int, color: Int = -1)
@Stub expect fun GuiGraphics.drawTexture(
    texture: ResourceLocation, x: Int, y: Int, width: Int, height: Int,
    u0: Float = 0f, v0: Float = 0f, u1: Float = 1f, v1: Float = 1f,
    color: Int = -1
)

@Stub expect fun GuiGraphics.showTooltip(text: Component, maxWidth: Int = 170, force: Boolean = true)
@Stub expect fun GuiGraphics.showTooltip(text: Component, x: Int, y: Int, maxWidth: Int = 170, force: Boolean = true)

@Stub expect fun GuiGraphics.getTranslation(): Vector2f
@Stub expect fun GuiGraphics.getScale(): Vector2f

@Stub expect fun GuiGraphics.applyBackgroundBlur()
