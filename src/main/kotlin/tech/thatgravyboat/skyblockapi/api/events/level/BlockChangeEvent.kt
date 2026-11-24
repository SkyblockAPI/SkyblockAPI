package tech.thatgravyboat.skyblockapi.api.events.level

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import tech.thatgravyboat.skyblockapi.api.area.mining.MiningBlock
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

/** Posted when the server changes a block. */
public data class BlockChangeEvent(val pos: BlockPos, val state: BlockState) : SkyBlockEvent()

/** Posted when the player mines a block. */
public data class BlockMinedEvent(val pos: BlockPos, val state: BlockState, val byMiningSpread: Boolean = false) : SkyBlockEvent()

/** Posted when the player mines an ore block. */
public data class MiningBlockMinedEvent(val pos: BlockPos, val block: MiningBlock, val byMiningSpread: Boolean = false) : SkyBlockEvent()
