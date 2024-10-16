package tech.thatgravyboat.skyblockapi.api.events.level

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

/** Posted when the server changes a block. */
class BlockChangeEvent(val pos: BlockPos, val state: BlockState) : SkyBlockEvent()

/** Posted when the player mines a block. */
class BlockMinedEvent(val pos: BlockPos, val state: BlockState) : SkyBlockEvent()
