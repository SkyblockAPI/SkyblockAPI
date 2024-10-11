package tech.thatgravyboat.skyblockapi.helpers

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.network.chat.FormattedText

object McFont {

    val self: Font get() = Minecraft.getInstance().font

    fun width(text: FormattedText): Int = self.width(text)
    fun width(text: String): Int = self.width(text)
    fun width(text: Char): Int = self.width(text.toString())
}
