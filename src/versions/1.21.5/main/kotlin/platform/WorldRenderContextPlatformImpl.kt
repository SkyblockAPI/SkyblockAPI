@file:Suppress("ACTUAL_WITHOUT_EXPECT")
package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.client.gui.Font
import net.minecraft.util.FormattedCharSequence
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import tech.thatgravyboat.skyblockapi.helpers.McFont

actual fun RenderWorldEvent.drawString(
    text: FormattedCharSequence,
    x: Float,
    y: Float,
    color: UInt,
    dropShadow: Boolean,
    displayMode: Font.DisplayMode,
    backgroundColor: UInt,
    light: Int,
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
