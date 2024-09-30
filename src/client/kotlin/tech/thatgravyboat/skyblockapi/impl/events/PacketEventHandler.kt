package tech.thatgravyboat.skyblockapi.impl.events

import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.level.BlockChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.level.PacketReceivedEvent
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
        }
    }


}
