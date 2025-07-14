package tech.thatgravyboat.skyblockapi.api.events.hypixel

import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket.PartyMember
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import java.util.*

data class PartyInfoEvent(
    val inParty: Boolean,
    val members: Map<UUID, PartyMember>
) : SkyBlockEvent()
