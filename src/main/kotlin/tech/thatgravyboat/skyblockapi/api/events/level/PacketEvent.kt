package tech.thatgravyboat.skyblockapi.api.events.level

import net.minecraft.network.protocol.Packet
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.events.base.EventBus

public sealed class PacketEvent(val packet: Packet<*>) : CancellableSkyBlockEvent() {

    override fun post(bus: EventBus): Boolean {
        return bus.post(this, null) {
            SkyBlockAPI.logger.error("Error occurred while invoking event subscription", it)
        }
    }
}

public class PacketSentEvent(packet: Packet<*>) : PacketEvent(packet)
public class PacketReceivedEvent(packet: Packet<*>) : PacketEvent(packet)
