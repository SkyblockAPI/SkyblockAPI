package tech.thatgravyboat.skyblockapi.api.data

import tech.thatgravyboat.skyblockapi.utils.text.TextColor

enum class SkyblockRarity {
    COMMON,
    UNCOMMON,
    RARE,
    EPIC,
    LEGENDARY,
    MYTHIC,
    DIVINE,
    ULTIMATE,
    SPECIAL,
    VERY_SPECIAL,
    ADMIN;

    companion object {

        fun fromColorOrNull(colorCode: Int): SkyblockRarity? {
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
