package tech.thatgravyboat.skyblockapi.api.data

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import tech.thatgravyboat.skyblockapi.utils.text.SkyBlockColor
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

enum class SkyBlockRarity(val color: Int, val skyBlockColor: Int, vararg val alternativeNames: String) {
    COMMON(TextColor.WHITE, SkyBlockColor.WHITE),
    UNCOMMON(TextColor.GREEN, SkyBlockColor.GREEN),
    RARE(TextColor.BLUE, SkyBlockColor.BLUE),
    EPIC(TextColor.DARK_PURPLE, SkyBlockColor.DARK_PURPLE),
    LEGENDARY(TextColor.GOLD, SkyBlockColor.GOLD),
    MYTHIC(TextColor.LIGHT_PURPLE, SkyBlockColor.LIGHT_PURPLE),
    DIVINE(TextColor.AQUA, SkyBlockColor.AQUA, "SUPREME"),
    ULTIMATE(TextColor.DARK_RED, SkyBlockColor.DARK_RED),
    SPECIAL(TextColor.RED, SkyBlockColor.RED),
    VERY_SPECIAL(TextColor.RED, SkyBlockColor.RED),
    ADMIN(TextColor.RED, SkyBlockColor.RED),
    ;

    val displayName: String = toFormattedName()
    val displayText: Component = Text.of(displayName) { this.color = this@SkyBlockRarity.color }

    override fun toString(): String = displayName

    companion object {
        fun fromNameOrNull(name: String) = entries.find { r -> r.name.equals(name, true) || r.alternativeNames.any { name.equals(it, true) } }
        fun fromName(name: String) = fromNameOrNull(name) ?: COMMON

        internal fun fromColorOrNull(colorCode: Int): SkyBlockRarity? {
            return entries.find { it.color == colorCode || it.skyBlockColor == colorCode }
        }
    }
}
