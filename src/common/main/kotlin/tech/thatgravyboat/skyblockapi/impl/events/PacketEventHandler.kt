package tech.thatgravyboat.skyblockapi.impl.events

import com.google.common.cache.CacheBuilder
import me.owdding.ktmodules.Module
import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.game.*
import net.minecraft.world.level.block.state.BlockState
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.level.BlockChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.level.PacketReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.level.PacketSentEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerCloseEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

private const val PLAYER_HOTBAR_CONTAINER_ID = 0
private const val PLAYER_INVENTORY_CONTAINER_ID = -2
private const val FIRST_HOTBAR_SLOT = 36

@Module
object PacketEventHandler {
    private val lastBlockChanges = CacheBuilder.newBuilder()
        .maximumSize(15)
        .expireAfterWrite(1.seconds.toJavaDuration())
        .build<BlockPos, Pair<BlockState, BlockState>>()

    private var lastContainerCloseId: Int? = null

    @Subscription
    fun onPacketSend(event: PacketSentEvent) {
        when (event.packet) {
            is ServerboundContainerClosePacket -> {
                lastContainerCloseId = event.packet.containerId
                ContainerCloseEvent.post()
            }
        }
    }

    @Subscription
    fun onPacketReceived(event: PacketReceivedEvent) {
        val packet = event.packet
        when (packet) {
            is ClientboundBlockUpdatePacket -> postBlockChange(packet.pos, packet.blockState)
            is ClientboundSectionBlocksUpdatePacket -> packet.runUpdates { mutablePos, state -> postBlockChange(mutablePos.immutable(), state) }
            is ClientboundContainerSetContentPacket -> {
                McClient.runNextTick {
                    val container = McScreen.asMenu?.takeIf { it.menu?.containerId == packet.containerId() } ?: return@runNextTick
                    ContainerInitializedEvent(packet.items(), container).post()
                }
            }

            is ClientboundContainerClosePacket -> {
                if (packet.containerId == lastContainerCloseId) return
                McClient.runNextTick(ContainerCloseEvent::post)
            }
        }
    }

    private fun postBlockChange(pos: BlockPos, new: BlockState) {
        if (!McLevel.hasLevel) return
        val old = McLevel[pos]

        val lastChance = lastBlockChanges.getIfPresent(pos)
        if (lastChance != null && lastChance.first == old && lastChance.second == new) {
            return
        }

        lastBlockChanges.put(pos, old to new)
        BlockChangeEvent(pos, new).post()
    }
}
