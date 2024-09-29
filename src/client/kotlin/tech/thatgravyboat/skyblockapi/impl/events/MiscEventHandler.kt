package tech.thatgravyboat.skyblockapi.impl.events

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.modules.Module

@Module
object MiscEventHandler {

    init {
        ClientTickEvents.END_CLIENT_TICK.register {
            TickEvent.post(SkyBlockAPI.eventBus)
        }
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            RegisterCommandsEvent(dispatcher).post(SkyBlockAPI.eventBus)
        }
    }
}
