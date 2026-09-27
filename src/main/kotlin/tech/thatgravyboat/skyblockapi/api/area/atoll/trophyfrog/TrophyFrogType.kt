package tech.thatgravyboat.skyblockapi.api.area.atoll.trophyfrog

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.trophy.TrophyTier
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockItemsRepo
import tech.thatgravyboat.skyblockapi.utils.lazy.registryBoundLazy
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

enum class TrophyFrogType(
    val displayName: Component,
    internalName: String = "",
) {
    COMMON_FROG(
        displayName = Text.of("Common Frog") {
            withStyle(ChatFormatting.WHITE)
        },
    ),
    LEAP_FROG(
        displayName = Text.of("Leap Frog") {
            withStyle(ChatFormatting.GREEN)
        },
    ),
    WETLANDS_FROG(
        displayName = Text.of("Wetlands Frog") {
            withStyle(ChatFormatting.GREEN)
        },
    ),
    REALITY_HOPPER(
        displayName = Text.of("Reality Hopper") {
            withStyle(ChatFormatting.GREEN)
        },
    ),
    EXPLODING_FROG(
        displayName = Text.of("Exploding Frog") {
            withStyle(ChatFormatting.GREEN)
        },
    ),
    BLESSED_FROG(
        displayName = Text.of("Blessed Frog") {
            withStyle(ChatFormatting.BLUE)
        },
    ),
    SEA_FROG(
        displayName = Text.of("Sea Frog") {
            withStyle(ChatFormatting.BLUE)
        },
    ),
    BULLFROG(
        displayName = Text.of("Bullfrog") {
            withStyle(ChatFormatting.BLUE)
        },
    ),
    TREE_FROG(
        displayName = Text.of("Tree Frog") {
            withStyle(ChatFormatting.DARK_PURPLE)
        },
    ),
    CAVE_FROG(
        displayName = Text.of("Cave Frog") {
            withStyle(ChatFormatting.DARK_PURPLE)
        },
    ),
    HIGHLANDS_FROG(
        displayName = Text.of("Highlands Frog") {
            withStyle(ChatFormatting.DARK_PURPLE)
        },
    ),
    PUDDLE_JUMPER(
        displayName = Text.of("Puddle Jumper") {
            withStyle(ChatFormatting.GOLD)
        },
    ),
    ;

    val internalName: String = internalName.takeUnless { it.isEmpty() } ?: name
    val strippedName = displayName.stripped

    val bronze by registryBoundLazy { SkyBlockItemsRepo.getItemStackOrDefault("${this.internalName}_BRONZE") }
    val silver by registryBoundLazy { SkyBlockItemsRepo.getItemStackOrDefault("${this.internalName}_SILVER") }
    val gold by registryBoundLazy { SkyBlockItemsRepo.getItemStackOrDefault("${this.internalName}_GOLD") }
    val diamond by registryBoundLazy { SkyBlockItemsRepo.getItemStackOrDefault("${this.internalName}_DIAMOND") }

    fun getItem(tier: TrophyTier): ItemStack {
        return when (tier) {
            TrophyTier.NONE -> bronze
            TrophyTier.BRONZE -> bronze
            TrophyTier.SILVER -> silver
            TrophyTier.GOLD -> gold
            TrophyTier.DIAMOND -> diamond
        }
    }

    fun getId(tier: TrophyTier, default: TrophyTier = TrophyTier.BRONZE): SkyBlockId = SkyBlockId.item("${this.internalName}_${tier.takeUnless { it == TrophyTier.NONE } ?: default}")

    companion object {
        fun getByInternalName(internalName: String): TrophyFrogType? {
            return entries.find { internalName.equals(it.internalName, ignoreCase = true) }
        }

        fun getByDisplayName(name: String): TrophyFrogType? {
            return entries.find { name.equals(it.strippedName, ignoreCase = true) }
        }
    }
}
