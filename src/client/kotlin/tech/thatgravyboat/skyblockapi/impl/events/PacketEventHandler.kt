package tech.thatgravyboat.skyblockapi.impl.events

import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.level.BlockChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.level.PacketReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryFullyLoadedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryItemChangeEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.modules.Module

@Module
object PacketEventHandler {

    @Subscription
    fun onPacketReceived(event: PacketReceivedEvent) {
        when (event.packet) {
            is ClientboundBlockUpdatePacket -> BlockChangeEvent(event.packet.pos, event.packet.blockState).post(SkyBlockAPI.eventBus)
            is ClientboundSectionBlocksUpdatePacket -> event.packet.runUpdates { pos, state ->
                BlockChangeEvent(pos.immutable(), state).post(SkyBlockAPI.eventBus)
            }

            is ClientboundContainerSetContentPacket -> {
                McClient.tell {
                    val container = McScreen.asMenu?.takeIf { it.menu?.containerId == event.packet.containerId } ?: return@tell
                    InventoryFullyLoadedEvent(event.packet.items, container.title).post(SkyBlockAPI.eventBus)
                }
            }

            is ClientboundContainerSetSlotPacket -> {
                McClient.tell {
                    val container = McScreen.asMenu?.takeIf { it.menu?.containerId == event.packet.containerId } ?: return@tell
                    val allItems = container.menu?.slots?.map { it.item } ?: emptyList()
                    InventoryItemChangeEvent(event.packet.item, event.packet.slot, container.title, allItems).post(SkyBlockAPI.eventBus)
                }
            }
        }
    }
}
