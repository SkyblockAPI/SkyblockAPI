package tech.thatgravyboat.skyblockapi.impl.events

import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.fabric.event.HypixelModAPICallback
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.location.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import kotlin.jvm.optionals.getOrNull

@Module
object HypixelEventHandler {

    init {
        HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket::class.java)
        HypixelModAPICallback.EVENT.register { event ->
            if (event is ClientboundLocationPacket) {
                ServerChangeEvent(
                    event.serverName,
                    event.serverType.getOrNull(),
                    event.lobbyName.getOrNull(),
                    event.mode.getOrNull(),
                    event.map.getOrNull(),
                ).post(SkyBlockAPI.eventBus)
            }
        }
    }
}
