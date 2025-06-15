package tech.thatgravyboat.skyblockapi.api.data

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.utils.extensions.toFormattedName
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

enum class SkyBlockRarity(val color: Int) {
    COMMON(TextColor.WHITE),
    UNCOMMON(TextColor.GREEN),
    RARE(TextColor.BLUE),
    EPIC(TextColor.DARK_PURPLE),
    LEGENDARY(TextColor.GOLD),
    MYTHIC(TextColor.LIGHT_PURPLE),
    DIVINE(TextColor.AQUA),
    ULTIMATE(TextColor.DARK_RED),
    SPECIAL(TextColor.RED),
    VERY_SPECIAL(TextColor.RED),
    ADMIN(TextColor.RED),
    ;

    val displayName: String = toFormattedName()
    val displayText: Component = Text.of(displayName) { this.color = this@SkyBlockRarity.color }

    override fun toString(): String = displayName

    companion object {
        fun fromNameOrNull(name: String) = entries.find { it.name.equals(name, true) }
        fun fromName(name: String) = fromNameOrNull(name) ?: COMMON

        internal fun fromColorOrNull(colorCode: Int): SkyBlockRarity? {
            return when (colorCode) {
                TextColor.WHITE -> COMMON
                TextColor.GREEN -> UNCOMMON
                TextColor.BLUE -> RARE
                TextColor.DARK_PURPLE -> EPIC
                TextColor.GOLD -> LEGENDARY
                TextColor.LIGHT_PURPLE -> MYTHIC
                TextColor.AQUA -> DIVINE
                else -> null
            }
        }
    }
}
