package tech.thatgravyboat.skyblockapi.api.datatype.defaults.trophy

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.utils.text.Text

public enum class TrophyTier(public val nameSuffix: Component, public val displayName: String) {
    NONE(
        nameSuffix = Component.empty(),
        displayName = "Total",
    ),
    BRONZE(
        nameSuffix = Text.of("BRONZE") {
            withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.BOLD)
        },
        displayName = "§8Bronze",
    ),
    SILVER(
        nameSuffix = Text.of("SILVER") {
            withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD)
        },
        displayName = "§7Silver",
    ),
    GOLD(
        nameSuffix = Text.of("GOLD") {
            withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
        },
        displayName = "§6Gold",
    ),
    DIAMOND(
        nameSuffix = Text.of("DIAMOND") {
            withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD)
        },
        displayName = "§bDiamond",
    );

    public companion object {
        public fun getByName(name: String): TrophyTier {
            return entries.find { it.name.equals(name, ignoreCase = true) } ?: NONE
        }
    }
}
