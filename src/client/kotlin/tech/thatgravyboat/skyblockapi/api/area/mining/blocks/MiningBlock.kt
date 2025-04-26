package tech.thatgravyboat.skyblockapi.api.area.mining.blocks

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import tech.thatgravyboat.skyblockapi.api.area.mining.GlaciteAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.level.BlockMinedEvent
import tech.thatgravyboat.skyblockapi.api.events.level.MiningBlockMinedEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland.*
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send

enum class Family {
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
    val family: Family,
) {
    // Vanilla blocks
    STONE(
        Blocks.STONE,
        { !SkyBlockIsland.inAnyIsland(CRYSTAL_HOLLOWS, MINESHAFT) },
        MiningFortuneType.BLOCK,
        Family.VANILLA_BLOCKS,
    ),
    COBBLESTONE(
        Blocks.COBBLESTONE,
        { !SkyBlockIsland.inAnyIsland(CRYSTAL_HOLLOWS, MINESHAFT) && !GlaciteAPI.inGlaciteTunnels() },
        MiningFortuneType.BLOCK,
        Family.VANILLA_BLOCKS,
    ),
    NETHERRACK(
        Blocks.NETHERRACK,
        { CRIMSON_ISLE.inIsland() },
        MiningFortuneType.BLOCK,
        Family.VANILLA_BLOCKS,
    ),
    END_STONE(
        Blocks.END_STONE,
        { THE_END.inIsland() },
        MiningFortuneType.BLOCK,
        Family.VANILLA_BLOCKS,
    ),
    OBSIDIAN(
        Blocks.OBSIDIAN,
        { SkyBlockIsland.inAnyIsland(THE_END, CRYSTAL_HOLLOWS, DEEP_CAVERNS) },
        MiningFortuneType.ORE,
        Family.VANILLA_BLOCKS,
    ),
    GRAVEL(
        Blocks.GRAVEL,
        { SPIDERS_DEN.inIsland() },
        MiningFortuneType.BLOCK,
        Family.VANILLA_BLOCKS,
    ),

    // Vanilla Ores
    COAL_ORE(Blocks.COAL_ORE, { true }, MiningFortuneType.ORE, Family.VANILLA_ORES),
    IRON_ORE(Blocks.IRON_ORE, { true }, MiningFortuneType.ORE, Family.VANILLA_ORES),
    GOLD_ORE(Blocks.GOLD_ORE, { true }, MiningFortuneType.ORE, Family.VANILLA_ORES),
    LAPIS_ORE(Blocks.LAPIS_ORE, { true }, MiningFortuneType.ORE, Family.VANILLA_ORES),
    DIAMOND_ORE(Blocks.DIAMOND_ORE, { true }, MiningFortuneType.ORE, Family.VANILLA_ORES),
    EMERALD_ORE(Blocks.EMERALD_ORE, { true }, MiningFortuneType.ORE, Family.VANILLA_ORES),
    NETHER_QUARTZ_ORE(Blocks.NETHER_QUARTZ_ORE, { true }, MiningFortuneType.ORE, Family.VANILLA_ORES),
    REDSTONE_ORE(Blocks.REDSTONE_ORE, { true }, MiningFortuneType.ORE, Family.VANILLA_ORES),

    // Nether
    GLOWSTONE(
        Blocks.GLOWSTONE,
        { CRIMSON_ISLE.inIsland() },
        MiningFortuneType.BLOCK,
        Family.EXTRA_NETHER,
    ),
    RED_SAND(
        Blocks.RED_SAND,
        { CRIMSON_ISLE.inIsland() },
        MiningFortuneType.BLOCK,
        Family.EXTRA_NETHER,
    ),
    MYCELIUM(
        Blocks.MYCELIUM,
        { CRIMSON_ISLE.inIsland() },
        MiningFortuneType.BLOCK,
        Family.EXTRA_NETHER,
    ),
    SULPHUR(
        Blocks.SPONGE,
        { CRIMSON_ISLE.inIsland() },
        MiningFortuneType.ORE,
        Family.EXTRA_NETHER,
    ),

    // TODO: Hard Stone

    // Mithril Family
    LOW_TIER_MITHRIL(
        listOf(Blocks.GRAY_TERRACOTTA, Blocks.GRAY_WOOL, Blocks.GRAY_TERRACOTTA),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        MiningFortuneType.DWARVEN_METAL,
        Family.MITHRIL,
    ),
    MID_TIER_MITHRIL(
        listOf(Blocks.DARK_PRISMARINE, Blocks.PRISMARINE, Blocks.PRISMARINE_BRICKS),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT, CRYSTAL_HOLLOWS) },
        MiningFortuneType.DWARVEN_METAL,
        Family.MITHRIL,
    ),
    HIGH_TIER_MITHRIL(
        Blocks.LIGHT_BLUE_WOOL,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT, CRYSTAL_HOLLOWS) },
        MiningFortuneType.DWARVEN_METAL,
        Family.MITHRIL,
    ),
    TITANIUM(
        Blocks.POLISHED_DIORITE,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        MiningFortuneType.DWARVEN_METAL,
        Family.MITHRIL,
    ),

    // Gemstones
    RUBY(
        listOf(Blocks.RED_STAINED_GLASS, Blocks.RED_STAINED_GLASS_PANE),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS, MINESHAFT) },
        MiningFortuneType.GEMSTONE,
        Family.GEMSTONES,
    ),
    SAPPHIRE(
        listOf(Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS, MINESHAFT) },
        MiningFortuneType.GEMSTONE,
        Family.GEMSTONES,
    ),
    JADE(
        listOf(Blocks.LIME_STAINED_GLASS, Blocks.LIME_STAINED_GLASS_PANE),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS, MINESHAFT) },
        MiningFortuneType.GEMSTONE,
        Family.GEMSTONES,
    ),
    AMBER(
        listOf(Blocks.ORANGE_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS_PANE),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS, MINESHAFT) },
        MiningFortuneType.GEMSTONE,
        Family.GEMSTONES,
    ),
    AMETHYST(
        listOf(Blocks.PURPLE_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS_PANE),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS, MINESHAFT) },
        MiningFortuneType.GEMSTONE,
        Family.GEMSTONES,
    ),
    TOPAZ(
        listOf(Blocks.YELLOW_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS_PANE),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS, MINESHAFT) },
        MiningFortuneType.GEMSTONE,
        Family.GEMSTONES,
    ),
    JASPER(
        listOf(Blocks.BROWN_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS_PANE),
        { SkyBlockIsland.inAnyIsland(CRYSTAL_HOLLOWS, MINESHAFT) },
        MiningFortuneType.GEMSTONE,
        Family.GEMSTONES,
    ),
    OPAL(
        listOf(Blocks.WHITE_STAINED_GLASS, Blocks.WHITE_STAINED_GLASS_PANE),
        { SkyBlockIsland.inAnyIsland(CRIMSON_ISLE, MINESHAFT) },
        MiningFortuneType.GEMSTONE,
        Family.GEMSTONES,
    ),
    PERIDOT(
        listOf(Blocks.GREEN_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS_PANE),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        MiningFortuneType.GEMSTONE,
        Family.GEMSTONES,
    ),
    CITRINE(
        listOf(Blocks.BROWN_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS_PANE),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        MiningFortuneType.GEMSTONE,
        Family.GEMSTONES,
    ),
    ONYX(
        listOf(Blocks.BLACK_STAINED_GLASS, Blocks.BLACK_STAINED_GLASS_PANE),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        MiningFortuneType.GEMSTONE,
        Family.GEMSTONES,
    ),
    AQUAMARINE(
        listOf(Blocks.BLUE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS_PANE),
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        MiningFortuneType.GEMSTONE,
        Family.GEMSTONES,
    ),

    // Pure Ores
    PURE_COAL(
        Blocks.COAL_BLOCK,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS) },
        MiningFortuneType.ORE,
        Family.PURE_ORES,
    ),
    PURE_IRON(
        Blocks.IRON_BLOCK,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS) },
        MiningFortuneType.ORE,
        Family.PURE_ORES,
    ),
    PURE_GOLD(
        Blocks.GOLD_BLOCK,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS) },
        MiningFortuneType.ORE,
        Family.PURE_ORES,
    ),
    PURE_LAPIS(
        Blocks.LAPIS_BLOCK,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS) },
        MiningFortuneType.ORE,
        Family.PURE_ORES,
    ),
    PURE_DIAMOND(
        Blocks.DIAMOND_BLOCK,
        { SkyBlockIsland.inAnyIsland(DEEP_CAVERNS, DWARVEN_MINES, CRYSTAL_HOLLOWS) },
        MiningFortuneType.ORE,
        Family.PURE_ORES,
    ),
    PURE_EMERALD(
        Blocks.EMERALD_BLOCK,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, CRYSTAL_HOLLOWS) },
        MiningFortuneType.ORE,
        Family.PURE_ORES,
    ),
    PURE_NETHER_QUARTZ(
        Blocks.QUARTZ_BLOCK,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES) },
        MiningFortuneType.ORE,
        Family.PURE_ORES,
    ),

    // Glacite Family
    LOW_TIER_UMBER(
        Blocks.TERRACOTTA,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        MiningFortuneType.ORE,
        Family.GLACITE,
    ),
    MID_TIER_UMBER(
        Blocks.BROWN_TERRACOTTA,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        MiningFortuneType.ORE,
        Family.GLACITE,
    ),
    HIGH_TIER_UMBER(
        Blocks.SMOOTH_RED_SANDSTONE,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        MiningFortuneType.ORE,
        Family.GLACITE,
    ),
    LOW_TIER_TUNGSTEN(
        Blocks.COBBLESTONE,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        MiningFortuneType.ORE,
        Family.GLACITE,
    ),
    MID_TIER_TUNGSTEN(
        Blocks.CLAY,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        MiningFortuneType.ORE,
        Family.GLACITE,
    ),
    GLACITE(
        Blocks.PACKED_ICE,
        { SkyBlockIsland.inAnyIsland(DWARVEN_MINES, MINESHAFT) },
        MiningFortuneType.ORE,
        Family.GLACITE,
    ),
    ;

    constructor(block: Block, validArea: () -> Boolean, category: MiningFortuneType, family: Family) : this(
        listOf(block),
        validArea,
        category,
        family,
    )

    @Module
    companion object {
        @Subscription
        fun onBlockMine(event: BlockMinedEvent) {
            if (!SkyBlockIsland.inAnyIsland(
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
            ) return

            val blocks = entries.filter { it.validArea() }
            val block = blocks.find { it.blocks.contains(event.state.block) } ?: return

            MiningBlockMinedEvent(event.pos, block).post()
            Text.of("Player mined ${block.name}").send()
        }

        fun inDwarven() = DWARVEN_MINES.inIsland() && !GlaciteAPI.inGlaciteTunnels()
    }
}
