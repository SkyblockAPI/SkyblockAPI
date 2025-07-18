package tech.thatgravyboat.skyblockapi.platform

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.gui.Font
import net.minecraft.util.FormattedCharSequence
import tech.thatgravyboat.skyblockapi.helpers.McFont

actual fun WorldRenderContext.drawString(
    text: FormattedCharSequence,
    x: Float,
    y: Float,
    color: UInt,
    dropShadow: Boolean,
    displayMode: Font.DisplayMode,
    backgroundColor: UInt,
    light: Int
) {
    val consumers = this.consumers() ?: return
    val pose = this.matrixStack()?.last()?.pose() ?: return
    McFont.self.drawInBatch(
        text,
        x,y,
        color.toInt(),
        dropShadow,
        pose,
        consumers,
        displayMode,
        backgroundColor.toInt(),
        light
    )
}
