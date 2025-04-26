package tech.thatgravyboat.skyblockapi.api.area.mining

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.BlockHitResult
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.level.BlockMinedEvent
import tech.thatgravyboat.skyblockapi.api.events.level.MiningBlockMinedEvent
import tech.thatgravyboat.skyblockapi.api.events.location.AreaChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.IslandChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.render.RenderHudEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland.*
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.modules.Module

enum class MiningBlockFamily {
    VANILLA_BLOCKS,
    VANILLA_ORES,
    HARD_STONE,
    MITHRIL,
    GEMSTONES,
    PURE_ORES,
    GLACITE,
    EXTRA_NETHER,
    ;

    fun getBlocks() = MiningBlock.entries.filter { it.family == this }.flatMap { it.blocks }
}

enum class MiningFortuneType {
    GEMSTONE,
    DWARVEN_METAL,
    ORE,
    BLOCK,
}

enum class MiningBlock(
    val blocks: List<Block>,
    val validArea: () -> Boolean,
    val category: MiningFortuneType,
    val family: MiningBlockFamily,
) {
    // Vanilla blocks
    STONE(
        Blocks.STONE,
        { !SkyBlockIsland.inAnyIsland(CRYSTAL_HOLLOWS, MINESHAFT) },
        MiningFortuneType.BLOCK,
        MiningBlockFamily.VANILLA_BLOCKS,
    ),
    COBBLESTONE(
        Blocks.COBBLESTONE,
        { !SkyBlockIsland.inAnyIsland(CRYSTAL_HOLLOWS, MINESHAFT) && !GlaciteAPI.inGlaciteTunnels() },
        MiningFortuneType.BLOCK,
        MiningBlockFamily.VANILLA_BLOCKS,
    ),
    NETHERRACK(
        Blocks.NETHERRACK,
        { CRIMSON_ISLE.inIsland() },
        MiningFortuneType.BLOCK,
        MiningBlockFamily.VANILLA_BLOCKS,
    ),
    END_STONE(
        Blocks.END_STONE,
        { THE_END.inIsland() },
        MiningFortuneType.BLOCK,
        MiningBlockFamily.VANILLA_BLOCKS,
    ),
    OBSIDIAN(
        Blocks.OBSIDIAN,
        { SkyBlockIsland.inAnyIsland(THE_END, CRYSTAL_HOLLOWS, DEEP_CAVERNS) },
        MiningFortuneType.ORE,
        MiningBlockFamily.VANILLA_BLOCKS,
    ),
    GRAVEL(
        Blocks.GRAVEL,
        { SPIDERS_DEN.inIsland() },
        MiningFortuneType.BLOCK,
        MiningBlockFamily.VANILLA_BLOCKS,
    ),

    // Vanilla Ores
    COAL_ORE(Blocks.COAL_ORE, { true }, MiningFortuneType.ORE, MiningBlockFamily.VANILLA_ORES),
    IRON_ORE(Blocks.IRON_ORE, { true }, MiningFortuneType.ORE, MiningBlockFamily.VANILLA_ORES),
    GOLD_ORE(Blocks.GOLD_ORE, { true }, MiningFortuneType.ORE, MiningBlockFamily.VANILLA_ORES),
    LAPIS_ORE(Blocks.LAPIS_ORE, { true }, MiningFortuneType.ORE, MiningBlockFamily.VANILLA_ORES),
    DIAMOND_ORE(Blocks.DIAMOND_ORE, { true }, MiningFortuneType.ORE, MiningBlockFamily.VANILLA_ORES),
    EMERALD_ORE(Blocks.EMERALD_ORE, { true }, MiningFortuneType.ORE, MiningBlockFamily.VANILLA_ORES),
    NETHER_QUARTZ_ORE(Blocks.NETHER_QUARTZ_ORE, { true }, MiningFortuneType.ORE, MiningBlockFamily.VANILLA_ORES),
    REDSTONE_ORE(Blocks.REDSTONE_ORE, { true }, MiningFortuneType.ORE, MiningBlockFamily.VANILLA_ORES),

    // Nether
    GLOWSTONE(
        Blocks.GLOWSTONE,
        { CRIMSON_ISLE.inIsland() },
        MiningFortuneType.BLOCK,
        MiningBlockFamily.EXTRA_NETHER,
    ),
    RED_SAND(
        Blocks.RED_SAND,
        { CRIMSON_ISLE.inIsland() },
        MiningFortuneType.BLOCK,
        MiningBlockFamily.EXTRA_NETHER,
    ),
    MYCELIUM(
        Blocks.MYCELIUM,
        { CRIMSON_ISLE.inIsland() },
        MiningFortuneType.BLOCK,
        MiningBlockFamily.EXTRA_NETHER,
    ),
    SULPHUR(
        Blocks.SPONGE,
        { CRIMSON_ISLE.inIsland() },
        MiningFortuneType.ORE,
        MiningBlockFamily.EXTRA_NETHER,
    ),

    // Hard Stone
    HARD_STONE_CRYSTAL_HOLLOWS(
        // There are probably more but mostly useless
        listOf(Blocks.STONE, Blocks.CLAY, Blocks.COBBLESTONE, Blocks.GRAY_WOOL, Blocks.LIGHT_GRAY_WOOL, Blocks.CYAN_TERRACOTTA),
        { CRYSTAL_HOLLOWS.inIsland() },
        MiningFortuneType.BLOCK,
        MiningBlockFamily.HARD_STONE,
    ),
    HARD_STONE_GLACITE_TUNNELS(
        listOf(Blocks.INFESTED_STONE, Blocks.LIGHT_GRAY_WOOL),
        { GlaciteAPI.inGlaciteTunnels() },
        MiningFortuneType.BLOCK,
        MiningBlockFamily.HARD_STONE,
    ),
    HARD_STONE_MINESHAFT(
        listOf(Blocks.STONE, Blocks.LIGHT_GRAY_WOOL),
        { SkyBlockIsland.inAnyIsland(MINESHAFT) },
        MiningFortuneType.BLOCK,
        MiningBlockFamily.HARD_STONE,
    ),

    // Mithril Family
    LOW_TIER_MITHRIL(
        listOf(Blocks.GRAY_TERRACOTTA, Blocks.GRAY_WOOL, Blocks.GRAY_TERRACOTTA),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        MiningFortuneType.DWARVEN_METAL,
        MiningBlockFamily.MITHRIL,
    ),
    MID_TIER_MITHRIL(
        listOf(Blocks.DARK_PRISMARINE, Blocks.PRISMARINE, Blocks.PRISMARINE_BRICKS),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT, CRYSTAL_HOLLOWS) },
        MiningFortuneType.DWARVEN_METAL,
        MiningBlockFamily.MITHRIL,
    ),
    HIGH_TIER_MITHRIL(
        Blocks.LIGHT_BLUE_WOOL,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT, CRYSTAL_HOLLOWS) },
        MiningFortuneType.DWARVEN_METAL,
        MiningBlockFamily.MITHRIL,
    ),
    TITANIUM(
        Blocks.POLISHED_DIORITE,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        MiningFortuneType.DWARVEN_METAL,
        MiningBlockFamily.MITHRIL,
    ),

    // Gemstones
    RUBY(
        listOf(Blocks.RED_STAINED_GLASS, Blocks.RED_STAINED_GLASS_PANE),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS, MINESHAFT) },
        MiningFortuneType.GEMSTONE,
        MiningBlockFamily.GEMSTONES,
    ),
    SAPPHIRE(
        listOf(Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS, MINESHAFT) },
        MiningFortuneType.GEMSTONE,
        MiningBlockFamily.GEMSTONES,
    ),
    JADE(
        listOf(Blocks.LIME_STAINED_GLASS, Blocks.LIME_STAINED_GLASS_PANE),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS, MINESHAFT) },
        MiningFortuneType.GEMSTONE,
        MiningBlockFamily.GEMSTONES,
    ),
    AMBER(
        listOf(Blocks.ORANGE_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS_PANE),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS, MINESHAFT) },
        MiningFortuneType.GEMSTONE,
        MiningBlockFamily.GEMSTONES,
    ),
    AMETHYST(
        listOf(Blocks.PURPLE_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS_PANE),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS, MINESHAFT) },
        MiningFortuneType.GEMSTONE,
        MiningBlockFamily.GEMSTONES,
    ),
    TOPAZ(
        listOf(Blocks.YELLOW_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS_PANE),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS, MINESHAFT) },
        MiningFortuneType.GEMSTONE,
        MiningBlockFamily.GEMSTONES,
    ),
    JASPER(
        listOf(Blocks.BROWN_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS_PANE),
        { SkyBlockIsland.inAnyIsland(CRYSTAL_HOLLOWS, MINESHAFT) },
        MiningFortuneType.GEMSTONE,
        MiningBlockFamily.GEMSTONES,
    ),
    OPAL(
        listOf(Blocks.WHITE_STAINED_GLASS, Blocks.WHITE_STAINED_GLASS_PANE),
        { SkyBlockIsland.inAnyIsland(CRIMSON_ISLE, MINESHAFT) },
        MiningFortuneType.GEMSTONE,
        MiningBlockFamily.GEMSTONES,
    ),
    PERIDOT(
        listOf(Blocks.GREEN_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS_PANE),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        MiningFortuneType.GEMSTONE,
        MiningBlockFamily.GEMSTONES,
    ),
    CITRINE(
        listOf(Blocks.BROWN_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS_PANE),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        MiningFortuneType.GEMSTONE,
        MiningBlockFamily.GEMSTONES,
    ),
    ONYX(
        listOf(Blocks.BLACK_STAINED_GLASS, Blocks.BLACK_STAINED_GLASS_PANE),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        MiningFortuneType.GEMSTONE,
        MiningBlockFamily.GEMSTONES,
    ),
    AQUAMARINE(
        listOf(Blocks.BLUE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS_PANE),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        MiningFortuneType.GEMSTONE,
        MiningBlockFamily.GEMSTONES,
    ),

    // Pure Ores
    PURE_COAL(
        Blocks.COAL_BLOCK,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS) },
        MiningFortuneType.ORE,
        MiningBlockFamily.PURE_ORES,
    ),
    PURE_IRON(
        Blocks.IRON_BLOCK,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS) },
        MiningFortuneType.ORE,
        MiningBlockFamily.PURE_ORES,
    ),
    PURE_GOLD(
        Blocks.GOLD_BLOCK,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS) },
        MiningFortuneType.ORE,
        MiningBlockFamily.PURE_ORES,
    ),
    PURE_LAPIS(
        Blocks.LAPIS_BLOCK,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS) },
        MiningFortuneType.ORE,
        MiningBlockFamily.PURE_ORES,
    ),
    PURE_DIAMOND(
        Blocks.DIAMOND_BLOCK,
        { SkyBlockIsland.inAnyIsland(DEEP_CAVERNS, DWARVEN_MINES, CRYSTAL_HOLLOWS) },
        MiningFortuneType.ORE,
        MiningBlockFamily.PURE_ORES,
    ),
    PURE_EMERALD(
        Blocks.EMERALD_BLOCK,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS) },
        MiningFortuneType.ORE,
        MiningBlockFamily.PURE_ORES,
    ),
    PURE_NETHER_QUARTZ(
        Blocks.QUARTZ_BLOCK,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES) },
        MiningFortuneType.ORE,
        MiningBlockFamily.PURE_ORES,
    ),

    // Glacite Family
    LOW_TIER_UMBER(
        Blocks.TERRACOTTA,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        MiningFortuneType.ORE,
        MiningBlockFamily.GLACITE,
    ),
    MID_TIER_UMBER(
        Blocks.BROWN_TERRACOTTA,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        MiningFortuneType.ORE,
        MiningBlockFamily.GLACITE,
    ),
    HIGH_TIER_UMBER(
        Blocks.SMOOTH_RED_SANDSTONE,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        MiningFortuneType.ORE,
        MiningBlockFamily.GLACITE,
    ),
    LOW_TIER_TUNGSTEN(
        listOf(Blocks.INFESTED_COBBLESTONE, Blocks.COBBLESTONE),
        { GlaciteAPI.inGlaciteTunnels() },
        MiningFortuneType.ORE,
        MiningBlockFamily.GLACITE,
    ),
    HIGH_TIER_TUNGSTEN(
        Blocks.CLAY,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        MiningFortuneType.ORE,
        MiningBlockFamily.GLACITE,
    ),
    GLACITE(
        Blocks.PACKED_ICE,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        MiningFortuneType.ORE,
        MiningBlockFamily.GLACITE,
    ),
    ;

    constructor(block: Block, validArea: () -> Boolean, category: MiningFortuneType, family: MiningBlockFamily) : this(
        listOf(block),
        validArea,
        category,
        family,
    )

    @Module
    companion object {
        private val MINING_ISLANDS = listOf(
            HUB,
            GOLD_MINES,
            DEEP_CAVERNS,
            DWARVEN_MINES,
            MINESHAFT,
            CRYSTAL_HOLLOWS,
            SPIDERS_DEN,
            THE_END,
            CRIMSON_ISLE,
        )

        var currentlyActiveBlocks = listOf<MiningBlock>()
            private set

        private var debugToggle = false

        @Subscription
        fun onIslandChange(event: IslandChangeEvent) {
            currentlyActiveBlocks = entries.filter { it.validArea() }
        }

        @Subscription
        fun onAreaChange(event: AreaChangeEvent) {
            currentlyActiveBlocks = entries.filter { it.validArea() }
        }

        @Subscription
        fun onBlockMine(event: BlockMinedEvent) {
            if (!SkyBlockIsland.inAnyIsland(MINING_ISLANDS)) return

            val block = currentlyActiveBlocks.find { it.blocks.contains(event.state.block) } ?: return

            MiningBlockMinedEvent(event.pos, block).post()
        }

        @Subscription
        fun onRender(event: RenderHudEvent) {
            if (!debugToggle) return
            val lookingAt = McClient.self.cameraEntity?.pick(20.0, 0f, false) as? BlockHitResult ?: return
            val block = currentlyActiveBlocks.find { it.blocks.contains(McLevel[lookingAt.blockPos].block) } ?: return
            event.graphics.drawString(McFont.self, "Looking at: ${block.name}", 8, 8, 0xFFFFFF)
        }

        @Subscription
        fun onCommandRegistration(event: RegisterCommandsEvent) {
            event.register("sbapi mining") {
                callback {
                    debugToggle = !debugToggle
                }
            }
        }
    }
}
