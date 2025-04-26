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
        { !SkyBlockIsland.inAnyIsland(CRYSTAL_HOLLOWS, MINESHAFT) },
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

    // Hard Stone

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

    // Pure Ores

    // Glacite Family
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
