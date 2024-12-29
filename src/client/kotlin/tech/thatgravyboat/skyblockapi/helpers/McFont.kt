package tech.thatgravyboat.skyblockapi.helpers

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.network.chat.FormattedText
import net.minecraft.util.FormattedCharSequence

object McFont {

    val self: Font get() = Minecraft.getInstance().font

    val height: Int get() = self.lineHeight

    fun width(text: FormattedText): Int = self.width(text)
    fun width(text: FormattedCharSequence): Int = self.width(text)
    fun width(text: String): Int = self.width(text)
    fun width(text: Char): Int = self.width(text.toString())

    fun split(text: FormattedText, maxWidth: Int): List<FormattedCharSequence> = self.split(text, maxWidth)
}
