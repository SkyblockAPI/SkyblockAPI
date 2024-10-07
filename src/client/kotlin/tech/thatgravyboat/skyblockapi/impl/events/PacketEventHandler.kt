package tech.thatgravyboat.skyblockapi.impl.events

import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.level.BlockChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.level.PacketReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.PlayerHotbarChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.PlayerInventoryChangeEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.modules.Module

private const val PLAYER_HOTBAR_CONTAINER_ID = 0
private const val PLAYER_INVENTORY_CONTAINER_ID = -2
private const val FIRST_HOTBAR_SLOT = 36

@Module
object PacketEventHandler {

    @Subscription
    fun onPacketReceived(event: PacketReceivedEvent) {
        when (event.packet) {
            is ClientboundBlockUpdatePacket -> BlockChangeEvent(event.packet.pos, event.packet.blockState).post()
            is ClientboundSectionBlocksUpdatePacket -> event.packet.runUpdates { pos, state ->
                BlockChangeEvent(pos.immutable(), state).post()
            }

            is ClientboundContainerSetContentPacket -> {
                McClient.tell {
                    val container = McScreen.asMenu?.takeIf { it.menu?.containerId == event.packet.containerId } ?: return@tell
                    ContainerInitializedEvent(event.packet.items, container.title).post()
                }
            }
            is ClientboundContainerSetSlotPacket -> {
                McClient.tell {
                    val containerId = event.packet.containerId
                    when (containerId) {
                        PLAYER_HOTBAR_CONTAINER_ID -> {
                            PlayerHotbarChangeEvent(event.packet.slot - FIRST_HOTBAR_SLOT, event.packet.item).post()
                        }
                        PLAYER_INVENTORY_CONTAINER_ID -> PlayerInventoryChangeEvent(event.packet.slot, event.packet.item).post()
                        else -> {
                            val container = McScreen.asMenu?.takeIf { it.menu?.containerId == containerId } ?: return@tell
                            ContainerChangeEvent(event.packet.item, event.packet.slot, container.title).post()
                        }
                    }
                }
            }
        }
    }
}
