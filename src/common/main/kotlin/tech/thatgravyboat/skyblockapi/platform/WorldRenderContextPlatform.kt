package tech.thatgravyboat.skyblockapi.platform

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.LightTexture
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.util.FormattedCharSequence
import net.msrandom.stub.Stub
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import tech.thatgravyboat.skyblockapi.utils.text.Text

fun RenderWorldEvent.drawString(
    text: String,
    x: Float,
    y: Float,
    color: UInt,
    dropShadow: Boolean = true,
    displayMode: Font.DisplayMode = Font.DisplayMode.SEE_THROUGH,
    backgroundColor: UInt = 0u,
    light: Int = LightTexture.FULL_BRIGHT,
) {
    this.drawString(Text.of(text), x, y, color, dropShadow, displayMode, backgroundColor, light)
}

fun RenderWorldEvent.drawString(
    text: Component,
    x: Float,
    y: Float,
    color: UInt,
    dropShadow: Boolean = true,
    displayMode: Font.DisplayMode = Font.DisplayMode.SEE_THROUGH,
    backgroundColor: UInt = 0u,
    light: Int = LightTexture.FULL_BRIGHT,
) {
    val text = Language.getInstance().getVisualOrder(text)
    drawString(text, x, y, color, dropShadow, displayMode, backgroundColor, light)
}

fun RenderWorldEvent.drawString(
    text: FormattedCharSequence,
    x: Float,
    y: Float,
    color: UInt,
    dropShadow: Boolean = true,
    displayMode: Font.DisplayMode = Font.DisplayMode.SEE_THROUGH,
    backgroundColor: UInt = 0u,
    light: Int = LightTexture.FULL_BRIGHT,
) = this.ctx.drawString(text, x, y, color, dropShadow, displayMode, backgroundColor, light)

fun WorldRenderContext.drawString(
    text: String,
    x: Float,
    y: Float,
    color: UInt,
    dropShadow: Boolean = true,
    displayMode: Font.DisplayMode = Font.DisplayMode.SEE_THROUGH,
    backgroundColor: UInt = 0u,
    light: Int = LightTexture.FULL_BRIGHT,
) {
    this.drawString(Text.of(text), x, y, color, dropShadow, displayMode, backgroundColor, light)
}

fun WorldRenderContext.drawString(
    text: Component,
    x: Float,
    y: Float,
    color: UInt,
    dropShadow: Boolean = true,
    displayMode: Font.DisplayMode = Font.DisplayMode.SEE_THROUGH,
    backgroundColor: UInt = 0u,
    light: Int = LightTexture.FULL_BRIGHT,
) {
    val text = Language.getInstance().getVisualOrder(text)
    drawString(text, x, y, color, dropShadow, displayMode, backgroundColor, light)
}

@Stub
expect fun WorldRenderContext.drawString(
    text: FormattedCharSequence,
    x: Float,
    y: Float,
    color: UInt,
    dropShadow: Boolean = true,
    displayMode: Font.DisplayMode = Font.DisplayMode.SEE_THROUGH,
    backgroundColor: UInt = 0u,
    light: Int = LightTexture.FULL_BRIGHT,
)
