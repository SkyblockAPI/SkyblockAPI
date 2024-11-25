package tech.thatgravyboat.skyblockapi.api.events.level

import net.minecraft.network.protocol.Packet
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.EventBus
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

open class PacketEvent(val packet: Packet<*>) : SkyBlockEvent() {

    override fun post(bus: EventBus): Boolean {
        return bus.post(this, null) {
            SkyBlockAPI.logger.error("Error occurred while invoking event subscription", it)
        }
    }
}

class PacketSentEvent(packet: Packet<*>) : PacketEvent(packet)
class PacketReceivedEvent(packet: Packet<*>) : PacketEvent(packet)
