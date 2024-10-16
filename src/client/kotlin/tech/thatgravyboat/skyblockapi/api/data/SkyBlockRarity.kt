package tech.thatgravyboat.skyblockapi.api.data

import tech.thatgravyboat.skyblockapi.utils.text.TextColor

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
    ADMIN(TextColor.RED),;

    companion object {

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
