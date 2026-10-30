package tech.thatgravyboat.skyblockapi.api.area.isle.trophyfish

//? < 26.4 {
/*import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.utils.text.Text

@Deprecated("Use TrophyTier instead")
public enum class TrophyFishTier(public val nameSuffix: Component, public val displayName: String) {
    NONE(
        nameSuffix = Component.empty(),
        displayName = "Total",
    ),
    BRONZE(
        nameSuffix = Text.of("BRONZE") {
            withStyle(DARK_GRAY, BOLD)
        },
        displayName = "§8Bronze",
    ),
    SILVER(
        nameSuffix = Text.of("SILVER") {
            withStyle(GRAY, BOLD)
        },
        displayName = "§7Silver",
    ),
    GOLD(
        nameSuffix = Text.of("GOLD") {
            withStyle(ChatFormatting.GOLD, BOLD)
        },
        displayName = "§6Gold",
    ),
    DIAMOND(
        nameSuffix = Text.of("DIAMOND") {
            withStyle(AQUA, BOLD)
        },
        displayName = "§bDiamond",
    );

    public companion object {
        @Suppress("DEPRECATION")
        public fun getByName(name: String): TrophyFishTier {
            return entries.find { it.name.equals(name, ignoreCase = true) } ?: NONE
        }
    }
}*///?}
