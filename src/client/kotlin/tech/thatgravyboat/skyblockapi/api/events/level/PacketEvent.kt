package tech.thatgravyboat.skyblockapi.api.events.level

import net.minecraft.network.protocol.Packet
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

open class PacketEvent(val packet: Packet<*>) : SkyBlockEvent()

class PacketSentEvent(packet: Packet<*>) : PacketEvent(packet)
class PacketReceivedEvent(packet: Packet<*>) : PacketEvent(packet)
