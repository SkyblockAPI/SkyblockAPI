package tech.thatgravyboat.skyblockapi.api.data

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

@Suppress("unused")
enum class SkyBlockStat(
    val icon: Char,
    val color: Int,
    name: String? = null,
) {
    // Combat stats
    HEALTH('', TextColor.RED),
    DEFENSE('', TextColor.GREEN),
    STRENGTH('', TextColor.RED),
    INTELLIGENCE('', TextColor.AQUA),
    CRIT_DAMAGE('', TextColor.BLUE),
    CRIT_CHANCE('', TextColor.BLUE),
    ATTACK_SPEED('', TextColor.YELLOW, name = "Bonus Attack Speed"),
    ABILITY_DAMAGE('', TextColor.RED),
    TRUE_DEFENSE('', TextColor.WHITE),
    FEROCITY('', TextColor.RED),
    HEALTH_REGEN('', TextColor.RED),
    VITALITY('', TextColor.DARK_RED),
    MENDING('', TextColor.GREEN),
    SWING_RANGE('', TextColor.YELLOW),

    // Mining Stats
    BREAKING_POWER('', TextColor.DARK_GREEN),
    MINING_SPEED('', TextColor.GOLD),
    MINING_SPREAD('', TextColor.YELLOW),
    GEMSTONE_SPREAD('', TextColor.YELLOW),
    PRISTINE('', TextColor.DARK_PURPLE),
    GENERIC_MINING_FORTUNE('', TextColor.GOLD), // helper
    MINING_FORTUNE(GENERIC_MINING_FORTUNE),
    ORE_FORTUNE(GENERIC_MINING_FORTUNE),
    BLOCK_FORTUNE(GENERIC_MINING_FORTUNE),
    DWARVEN_METAL_FORTUNE(GENERIC_MINING_FORTUNE),
    GEMSTONE_FORTUNE(GENERIC_MINING_FORTUNE),

    // Farming Stats
    BONUS_PEST_CHANCE('', TextColor.DARK_GREEN),
    OVERBLOOM('', TextColor.YELLOW),
    GENERIC_FARMING_FORTUNE('', TextColor.GOLD), // helper
    FARMING_FORTUNE(GENERIC_FARMING_FORTUNE),
    WHEAT_FORTUNE(GENERIC_FARMING_FORTUNE),
    CARROT_FORTUNE(GENERIC_FARMING_FORTUNE),
    POTATO_FORTUNE(GENERIC_FARMING_FORTUNE),
    PUMPKIN_FORTUNE(GENERIC_FARMING_FORTUNE),
    SUGAR_CANE_FORTUNE(GENERIC_FARMING_FORTUNE),
    MELON_SLICE_FORTUNE(GENERIC_FARMING_FORTUNE),
    CACTUS_FORTUNE(GENERIC_FARMING_FORTUNE),
    COCOA_BEANS_FORTUNE(GENERIC_FARMING_FORTUNE),
    MUSHROOM_FORTUNE(GENERIC_FARMING_FORTUNE),
    NETHER_WART_FORTUNE(GENERIC_FARMING_FORTUNE),
    SUNFLOWER_FORTUNE(GENERIC_FARMING_FORTUNE),
    MOONFLOWER_FORTUNE(GENERIC_FARMING_FORTUNE),
    WILD_ROSE_FORTUNE(GENERIC_FARMING_FORTUNE),

    // Foraging Stats
    SWEEP('', TextColor.DARK_GREEN),
    GENERIC_FORAGING_FORTUNE('', TextColor.GOLD), // Helper
    FORAGING_FORTUNE(GENERIC_FORAGING_FORTUNE),
    FIG_FORTUNE(GENERIC_FORAGING_FORTUNE),
    MANGROVE_FORTUNE(GENERIC_FORAGING_FORTUNE),

    // Fishing Stats
    FISHING_SPEED('', TextColor.AQUA),
    SEA_CREATURE_CHANCE('', TextColor.DARK_AQUA),
    DOUBLE_HOOK_CHANCE('', TextColor.BLUE),
    TROPHY_CHANCE('', TextColor.GOLD),
    TREASURE_CHANCE('', TextColor.GOLD),

    // Misc Stats
    SPEED('', TextColor.WHITE),
    MAGIC_FIND('', TextColor.AQUA),
    PET_LUCK('', TextColor.LIGHT_PURPLE),
    HEAT_RESISTANCE('', TextColor.RED),
    COLD_RESISTANCE('', TextColor.AQUA),
    RESPIRATION('', TextColor.DARK_AQUA),
    PRESSURE_RESISTANCE('', TextColor.BLUE),
    FEAR('', TextColor.DARK_PURPLE),
    TRACKING('', TextColor.LIGHT_PURPLE),

    // Hunting Stats
    PULL('', TextColor.AQUA),
    HUNTER_FORTUNE('', TextColor.LIGHT_PURPLE),

    // Wisdom Stats
    GENERIC_WISDOM('☯', TextColor.DARK_AQUA), // helper
    COMBAT_WISDOM(GENERIC_WISDOM),
    MINING_WISDOM(GENERIC_WISDOM),
    FARMING_WISDOM(GENERIC_WISDOM),
    FORAGING_WISDOM(GENERIC_WISDOM),
    FISHING_WISDOM(GENERIC_WISDOM),
    ENCHANTING_WISDOM(GENERIC_WISDOM),
    ALCHEMY_WISDOM(GENERIC_WISDOM),
    CARPENTRY_WISDOM(GENERIC_WISDOM),
    RUNECRAFTING_WISDOM(GENERIC_WISDOM),
    SOCIAL_WISDOM(GENERIC_WISDOM),
    TAMING_WISDOM(GENERIC_WISDOM),
    HUNTING_WISDOM(GENERIC_WISDOM),

    // Misc Stats
    @Deprecated("Unused in game, use TROPHY_CHANCE instead.")
    TROPHY_FISH_CHANCE('♔', TextColor.GOLD),
    @Deprecated("Unused in game, use HUNTER_FORTUNE instead.")
    SYPHON_LUCK('♣', TextColor.LIGHT_PURPLE),
    ;

    constructor(stat: SkyBlockStat) : this(stat.icon, stat.color)

    private val displayName: String = name ?: toFormattedName()

    override fun toString(): String = displayName

    val displayText: Component = Text.of("$icon $displayName") {
        this@of.color = this@SkyBlockStat.color
    }

    companion object {
        fun fromName(name: String): SkyBlockStat? {
            return entries.find { it.displayName.equals(name, ignoreCase = true) }
        }
    }
}
