package tech.thatgravyboat.skyblockapi.api.area.isle.trophyfish

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.remote.RepoItemsAPI
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

enum class TrophyFishType(
    val displayName: Component,
    internalName: String = "",
) {
    SULPHUR_SKITTER(
        displayName = Text.of("Sulphur Skitter") {
            withStyle(ChatFormatting.WHITE)
        },
    ),
    OBFUSCATED_ONE(
        displayName = Text.of("Obfuscated 1") {
            withStyle(ChatFormatting.WHITE, ChatFormatting.OBFUSCATED)
        },
        internalName = "OBFUSCATED_FISH_1",
    ),
    STEAMING_HOT_FLOUNDER(
        displayName = Text.of("Steaming-Hot Flounder") {
            withStyle(ChatFormatting.WHITE)
        },
    ),
    GUSHER(
        displayName = Text.of("Gusher") {
            withStyle(ChatFormatting.WHITE)
        },
    ),
    BLOBFISH(
        displayName = Text.of("Blobfish") {
            withStyle(ChatFormatting.WHITE)
        },
    ),
    OBFUSCATED_TWO(
        displayName = Text.of("Obfuscated 2") {
            withStyle(ChatFormatting.GREEN, ChatFormatting.OBFUSCATED)
        },
        internalName = "OBFUSCATED_FISH_2",
    ),
    SLUGFISH(
        displayName = Text.of("Slugfish") {
            withStyle(ChatFormatting.GREEN)
        },
    ),
    FLYFISH(
        displayName = Text.of("Flyfish") {
            withStyle(ChatFormatting.GREEN)
        },
    ),
    OBFUSCATED_THREE(
        displayName = Text.of("Obfuscated 3") {
            withStyle(ChatFormatting.BLUE, ChatFormatting.OBFUSCATED)
        },
        internalName = "OBFUSCATED_FISH_3",
    ),
    LAVA_HORSE(
        displayName = Text.of("Lavahorse") {
            withStyle(ChatFormatting.BLUE)
        },
    ),
    MANA_RAY(
        displayName = Text.of("Mana Ray") {
            withStyle(ChatFormatting.BLUE)
        },
    ),
    VOLCANIC_STONEFISH(
        displayName = Text.of("Volcanic Stonefish") {
            withStyle(ChatFormatting.BLUE)
        },
    ),
    VANILLE(
        displayName = Text.of("Vanille") {
            withStyle(ChatFormatting.BLUE)
        },
    ),
    SKELETON_FISH(
        displayName = Text.of("Skeleton Fish") {
            withStyle(ChatFormatting.DARK_PURPLE)
        },
    ),
    MOLDFIN(
        displayName = Text.of("Moldfin") {
            withStyle(ChatFormatting.DARK_PURPLE)
        },
    ),
    SOUL_FISH(
        displayName = Text.of("Soul Fish") {
            withStyle(ChatFormatting.DARK_PURPLE)
        },
    ),
    KARATE_FISH(
        displayName = Text.of("Karate Fish") {
            withStyle(ChatFormatting.DARK_PURPLE)
        },
    ),
    GOLDEN_FISH(
        displayName = Text.of("Golden Fish") {
            withStyle(ChatFormatting.GOLD)
        },
    );

    val internalName: String = internalName.takeUnless { it.isEmpty() } ?: name
    val strippedName = displayName.stripped

    val bronze by lazy { RepoItemsAPI.getItem("${this.internalName}_BRONZE") }
    val silver by lazy { RepoItemsAPI.getItem("${this.internalName}_SILVER") }
    val gold by lazy { RepoItemsAPI.getItem("${this.internalName}_GOLD") }
    val diamond by lazy { RepoItemsAPI.getItem("${this.internalName}_DIAMOND") }

    fun getItem(tier: TrophyFishTier): ItemStack {
        return when (tier) {
            TrophyFishTier.NONE -> bronze
            TrophyFishTier.BRONZE -> bronze
            TrophyFishTier.SILVER -> silver
            TrophyFishTier.GOLD -> gold
            TrophyFishTier.DIAMOND -> diamond
        }
    }

    companion object {
        fun getByInternalName(internalName: String): TrophyFishType? {
            return entries.firstOrNull { internalName.equals(it.internalName, ignoreCase = true) }
        }

        fun getByDisplayName(name: String): TrophyFishType? {
            return entries.firstOrNull { name.equals(it.strippedName, ignoreCase = true) }
        }
    }
}
