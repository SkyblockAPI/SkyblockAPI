package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.LightTexture
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.util.FormattedCharSequence

import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import tech.thatgravyboat.skyblockapi.helpers.McFont
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
    drawText(text, x, y, color, dropShadow, displayMode, backgroundColor, light)
}

//? if = 1.21.5 {
/*@Deprecated("", replaceWith = ReplaceWith("drawText"))
fun RenderWorldEvent.drawString(
    text: FormattedCharSequence,
    x: Float,
    y: Float,
    color: UInt,
    dropShadow: Boolean = true,
    displayMode: Font.DisplayMode = Font.DisplayMode.SEE_THROUGH,
    backgroundColor: UInt = 0u,
    light: Int = LightTexture.FULL_BRIGHT,
) = drawText(text, x, y, color, dropShadow, displayMode, backgroundColor, light)
*///?}

fun RenderWorldEvent.drawText(
    text: FormattedCharSequence,
    x: Float,
    y: Float,
    color: UInt,
    dropShadow: Boolean = true,
    displayMode: Font.DisplayMode = Font.DisplayMode.SEE_THROUGH,
    backgroundColor: UInt = 0u,
    light: Int = LightTexture.FULL_BRIGHT,
) {
    McFont.self.drawInBatch(
        text,
        x, y,
        color.toInt(),
        dropShadow,
        this.poseStack.last().pose(),
        this.buffer,
        displayMode,
        backgroundColor.toInt(),
        light,
    )
}
