package tech.thatgravyboat.skyblockapi.impl.events

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.modules.Module

@Module
object MiscEventHandler {

    init {
        ClientTickEvents.END_CLIENT_TICK.register {
            TickEvent.post(SkyBlockAPI.eventBus)
        }
    }
}
