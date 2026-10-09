package tech.thatgravyboat.skyblockapi.helpers

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.network.chat.FormattedText
import net.minecraft.util.FormattedCharSequence

public object McFont {

    public val self: Font get() = Minecraft.getInstance().font

    public val height: Int get() = self.lineHeight

    public fun width(text: FormattedText): Int = self.width(text)
    public fun width(text: FormattedCharSequence): Int = self.width(text)
    public fun width(text: String): Int = self.width(text)
    public fun width(text: Char): Int = self.width(text.toString())

    public fun split(text: FormattedText, maxWidth: Int): List<FormattedCharSequence> = self.split(text, maxWidth)
}
