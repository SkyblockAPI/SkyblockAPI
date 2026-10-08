package tech.thatgravyboat.skyblockapi.api.area.mining

import me.owdding.ktmodules.Module
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.BlockHitResult
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.level.BlockMinedEvent
import tech.thatgravyboat.skyblockapi.api.events.level.MiningBlockMinedEvent
import tech.thatgravyboat.skyblockapi.api.events.location.AreaChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.IslandChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.render.RenderHudEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland.*
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.platform.drawString
import tech.thatgravyboat.skyblockapi.utils.debugToggle
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send

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

    val blocks by lazy { MiningBlock.entries.filter { it.family == this }.flatMap { it.blocks } }
}

enum class MiningFortuneType {
    GEMSTONE,
    DWARVEN_METAL,
    ORE,
    BLOCK,
}

private typealias Type = MiningFortuneType
private typealias Family = MiningBlockFamily

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
        Type.BLOCK,
        Family.VANILLA_BLOCKS,
    ),
    COBBLESTONE(
        Blocks.COBBLESTONE,
        { !SkyBlockIsland.inAnyIsland(CRYSTAL_HOLLOWS, MINESHAFT) && !GlaciteAPI.inGlaciteTunnels() },
        Type.BLOCK,
        Family.VANILLA_BLOCKS,
    ),
    NETHERRACK(
        Blocks.NETHERRACK,
        CRIMSON_ISLE::inIsland,
        Type.BLOCK,
        Family.VANILLA_BLOCKS,
    ),
    END_STONE(
        Blocks.END_STONE,
        THE_END::inIsland,
        Type.BLOCK,
        Family.VANILLA_BLOCKS,
    ),
    OBSIDIAN(
        Blocks.OBSIDIAN,
        { SkyBlockIsland.inAnyIsland(THE_END, CRYSTAL_HOLLOWS, DEEP_CAVERNS) },
        Type.ORE,
        Family.VANILLA_BLOCKS,
    ),
    GRAVEL(
        Blocks.GRAVEL,
        SPIDERS_DEN::inIsland,
        Type.BLOCK,
        Family.VANILLA_BLOCKS,
    ),

    // Vanilla Ores
    COAL_ORE(Blocks.COAL_ORE, { true }, Type.ORE, Family.VANILLA_ORES),
    IRON_ORE(Blocks.IRON_ORE, { true }, Type.ORE, Family.VANILLA_ORES),
    GOLD_ORE(Blocks.GOLD_ORE, { true }, Type.ORE, Family.VANILLA_ORES),
    LAPIS_ORE(Blocks.LAPIS_ORE, { true }, Type.ORE, Family.VANILLA_ORES),
    DIAMOND_ORE(Blocks.DIAMOND_ORE, { true }, Type.ORE, Family.VANILLA_ORES),
    EMERALD_ORE(Blocks.EMERALD_ORE, { true }, Type.ORE, Family.VANILLA_ORES),
    NETHER_QUARTZ_ORE(Blocks.NETHER_QUARTZ_ORE, { true }, Type.ORE, Family.VANILLA_ORES),
    REDSTONE_ORE(Blocks.REDSTONE_ORE, { true }, Type.ORE, Family.VANILLA_ORES),

    // Nether
    GLOWSTONE(
        Blocks.GLOWSTONE,
        CRIMSON_ISLE::inIsland,
        Type.BLOCK,
        Family.EXTRA_NETHER,
    ),
    RED_SAND(
        Blocks.RED_SAND,
        CRIMSON_ISLE::inIsland,
        Type.BLOCK,
        Family.EXTRA_NETHER,
    ),
    MYCELIUM(
        Blocks.MYCELIUM,
        CRIMSON_ISLE::inIsland,
        Type.BLOCK,
        Family.EXTRA_NETHER,
    ),
    SULPHUR(
        Blocks.SPONGE,
        CRIMSON_ISLE::inIsland,
        Type.ORE,
        Family.EXTRA_NETHER,
    ),

    // Hard Stone
    HARD_STONE_CRYSTAL_HOLLOWS(
        // There are probably more but mostly useless
        listOf(Blocks.STONE, Blocks.CLAY, Blocks.COBBLESTONE, Blocks.WOOL.gray, Blocks.WOOL.lightGray, Blocks.DYED_TERRACOTTA.cyan),
        CRYSTAL_HOLLOWS::inIsland,
        Type.BLOCK,
        Family.HARD_STONE,
    ),
    HARD_STONE_GLACITE_TUNNELS(
        listOf(Blocks.INFESTED_STONE, Blocks.WOOL.lightGray),
        GlaciteAPI::inGlaciteTunnels,
        Type.BLOCK,
        Family.HARD_STONE,
    ),
    HARD_STONE_MINESHAFT(
        listOf(Blocks.STONE, Blocks.WOOL.lightGray),
        MINESHAFT::inIsland,
        Type.BLOCK,
        Family.HARD_STONE,
    ),

    // Mithril Family
    LOW_TIER_MITHRIL(
        listOf(Blocks.DYED_TERRACOTTA.gray, Blocks.WOOL.gray),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        Type.DWARVEN_METAL,
        Family.MITHRIL,
    ),
    MID_TIER_MITHRIL(
        listOf(Blocks.DARK_PRISMARINE, Blocks.PRISMARINE, Blocks.PRISMARINE_BRICKS),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT, CRYSTAL_HOLLOWS) },
        Type.DWARVEN_METAL,
        Family.MITHRIL,
    ),
    HIGH_TIER_MITHRIL(
        Blocks.WOOL.lightBlue,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT, CRYSTAL_HOLLOWS) },
        Type.DWARVEN_METAL,
        Family.MITHRIL,
    ),
    TITANIUM(
        Blocks.POLISHED_DIORITE,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        Type.DWARVEN_METAL,
        Family.MITHRIL,
    ),

    // Gemstones
    RUBY(
        listOf(Blocks.STAINED_GLASS.red, Blocks.STAINED_GLASS_PANE.red),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS, MINESHAFT) },
        Type.GEMSTONE,
        Family.GEMSTONES,
    ),
    SAPPHIRE(
        listOf(Blocks.STAINED_GLASS.lightBlue, Blocks.STAINED_GLASS_PANE.lightBlue),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS, MINESHAFT) },
        Type.GEMSTONE,
        Family.GEMSTONES,
    ),
    JADE(
        listOf(Blocks.STAINED_GLASS.lime, Blocks.STAINED_GLASS_PANE.lime),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS, MINESHAFT) },
        Type.GEMSTONE,
        Family.GEMSTONES,
    ),
    AMBER(
        listOf(Blocks.STAINED_GLASS.orange, Blocks.STAINED_GLASS_PANE.orange),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS, MINESHAFT) },
        Type.GEMSTONE,
        Family.GEMSTONES,
    ),
    AMETHYST(
        listOf(Blocks.STAINED_GLASS.purple, Blocks.STAINED_GLASS_PANE.purple),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS, MINESHAFT) },
        Type.GEMSTONE,
        Family.GEMSTONES,
    ),
    TOPAZ(
        listOf(Blocks.STAINED_GLASS.yellow, Blocks.STAINED_GLASS_PANE.yellow),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS, MINESHAFT) },
        Type.GEMSTONE,
        Family.GEMSTONES,
    ),
    JASPER(
        listOf(Blocks.STAINED_GLASS.pink, Blocks.STAINED_GLASS_PANE.pink),
        { SkyBlockIsland.inAnyIsland(CRYSTAL_HOLLOWS, MINESHAFT) },
        Type.GEMSTONE,
        Family.GEMSTONES,
    ),
    OPAL(
        listOf(Blocks.STAINED_GLASS.white, Blocks.STAINED_GLASS_PANE.white),
        { SkyBlockIsland.inAnyIsland(CRIMSON_ISLE, MINESHAFT) },
        Type.GEMSTONE,
        Family.GEMSTONES,
    ),
    PERIDOT(
        listOf(Blocks.STAINED_GLASS.green, Blocks.STAINED_GLASS_PANE.green),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        Type.GEMSTONE,
        Family.GEMSTONES,
    ),
    CITRINE(
        listOf(Blocks.STAINED_GLASS.brown, Blocks.STAINED_GLASS_PANE.brown),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        Type.GEMSTONE,
        Family.GEMSTONES,
    ),
    ONYX(
        listOf(Blocks.STAINED_GLASS.black, Blocks.STAINED_GLASS_PANE.black),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        Type.GEMSTONE,
        Family.GEMSTONES,
    ),
    AQUAMARINE(
        listOf(Blocks.STAINED_GLASS.blue, Blocks.STAINED_GLASS_PANE.blue),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        Type.GEMSTONE,
        Family.GEMSTONES,
    ),

    // Pure Ores
    PURE_COAL(
        Blocks.COAL_BLOCK,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS) },
        Type.ORE,
        Family.PURE_ORES,
    ),
    PURE_IRON(
        Blocks.IRON_BLOCK,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS) },
        Type.ORE,
        Family.PURE_ORES,
    ),
    PURE_GOLD(
        Blocks.GOLD_BLOCK,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS) },
        Type.ORE,
        Family.PURE_ORES,
    ),
    PURE_LAPIS(
        Blocks.LAPIS_BLOCK,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS) },
        Type.ORE,
        Family.PURE_ORES,
    ),
    PURE_DIAMOND(
        Blocks.DIAMOND_BLOCK,
        { SkyBlockIsland.inAnyIsland(DEEP_CAVERNS, DWARVEN_MINES, CRYSTAL_HOLLOWS) },
        Type.ORE,
        Family.PURE_ORES,
    ),
    PURE_EMERALD(
        Blocks.EMERALD_BLOCK,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS) },
        Type.ORE,
        Family.PURE_ORES,
    ),
    PURE_NETHER_QUARTZ(
        Blocks.QUARTZ_BLOCK,
        DWARVEN_MINES::inIsland,
        Type.ORE,
        Family.PURE_ORES,
    ),

    // Glacite Family
    LOW_TIER_UMBER(
        Blocks.TERRACOTTA,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        Type.ORE,
        Family.GLACITE,
    ),
    MID_TIER_UMBER(
        Blocks.DYED_TERRACOTTA.brown,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        Type.ORE,
        Family.GLACITE,
    ),
    HIGH_TIER_UMBER(
        Blocks.SMOOTH_RED_SANDSTONE,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        Type.ORE,
        Family.GLACITE,
    ),
    LOW_TIER_TUNGSTEN(
        listOf(Blocks.INFESTED_COBBLESTONE, Blocks.COBBLESTONE),
        GlaciteAPI::inGlaciteTunnels,
        Type.ORE,
        Family.GLACITE,
    ),
    HIGH_TIER_TUNGSTEN(
        Blocks.CLAY,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        Type.ORE,
        Family.GLACITE,
    ),
    GLACITE(
        Blocks.PACKED_ICE,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        Type.ORE,
        Family.GLACITE,
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
        private val MINING_ISLANDS = setOf(
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
        var lastBrokenBlock: Pair<BlockPos, MiningBlock>? = null
            private set

        private val debug by debugToggle("mining_blocks", "Send messages when the player mines a MiningBlock.")

        @Subscription(AreaChangeEvent::class, IslandChangeEvent::class)
        fun onAreaChange() {
            currentlyActiveBlocks = entries.filter { it.validArea() }
        }

        @Subscription
        @OnlyOnSkyBlock
        fun onBlockMine(event: BlockMinedEvent) {
            if (!SkyBlockIsland.inAnyIsland(MINING_ISLANDS)) return

            val block = currentlyActiveBlocks.find { it.blocks.contains(event.state.block) } ?: return
            lastBrokenBlock = event.pos to block

            MiningBlockMinedEvent(event.pos, block, event.byMiningSpread).post()

            if (debug) Text.join(
                "Mined ${block.name}",
                if (event.byMiningSpread) " (with spread)" else null,
            ).send()
        }

        @Subscription
        @OnlyOnSkyBlock
        fun onRender(event: RenderHudEvent) {
            if (!debug) return
            val lookingAt = McClient.self.cameraEntity?.pick(20.0, 0f, false) as? BlockHitResult ?: return
            val block = McLevel[lookingAt.blockPos].block
            val miningBlock = currentlyActiveBlocks.find { it.blocks.contains(block) } ?: return
            event.graphics.drawString("Looking at: $miningBlock", 8, 8, 0xFFFFFF)
        }
    }
}
