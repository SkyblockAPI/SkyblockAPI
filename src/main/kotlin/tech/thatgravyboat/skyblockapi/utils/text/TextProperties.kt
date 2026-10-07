package tech.thatgravyboat.skyblockapi.utils.text

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.utils.extentions.stripColor

object TextProperties {
    val Component.width: Int get() = McFont.width(this)
    val Component.stripped: String get() = this.string.stripColor()
}
