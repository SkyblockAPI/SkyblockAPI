package tech.thatgravyboat.skyblockapi.api.area.isle.trophyfish

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.utils.text.Text

enum class TrophyFishRank(val displayName: Component) {
    NOVICE(displayName = Text.of("Novice") { withStyle(ChatFormatting.DARK_GRAY) }),
    ADEPT(displayName = Text.of("Adept") { withStyle(ChatFormatting.GRAY) }),
    EXPERT(displayName = Text.of("Expert") { withStyle(ChatFormatting.GOLD) }),
    MASTER(displayName = Text.of("Master") { withStyle(ChatFormatting.AQUA) });

    companion object {
        fun getById(id: Int): TrophyFishRank? {
            return entries.find { it.ordinal == id }
        }
    }
}
