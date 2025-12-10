@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.LightTexture
import net.minecraft.util.FormattedCharSequence
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent

@Deprecated("", replaceWith = ReplaceWith("drawText"))
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
