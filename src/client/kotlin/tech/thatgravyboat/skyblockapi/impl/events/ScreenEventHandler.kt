package tech.thatgravyboat.skyblockapi.impl.events

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenInitializedEvent
import tech.thatgravyboat.skyblockapi.modules.Module

@Module
object ScreenEventHandler {

    init {
        ScreenEvents.AFTER_INIT.register { _, screen, _, _ ->
            ScreenInitializedEvent(screen).post(SkyBlockAPI.eventBus)
        }
    }
}
