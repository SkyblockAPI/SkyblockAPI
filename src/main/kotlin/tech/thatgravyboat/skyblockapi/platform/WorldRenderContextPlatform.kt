package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.client.gui.Font
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.util.FormattedCharSequence
import net.minecraft.util.LightCoordsUtil
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
    light: Int = LightCoordsUtil.FULL_BRIGHT,
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
    light: Int = LightCoordsUtil.FULL_BRIGHT,
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
    light: Int = LightCoordsUtil.FULL_BRIGHT,
) {
    // TODO: fix "drawInBatch methods are removed"
    //? < 26.2 {
    /*McFont.self.drawInBatch(
        text,
        x, y,
        color.toInt(),
        dropShadow,
        this.poseStack.last().pose(),
        //? < 26.2
        //this.buffer,
        displayMode,
        backgroundColor.toInt(),
        light,
    )*///? }
}
