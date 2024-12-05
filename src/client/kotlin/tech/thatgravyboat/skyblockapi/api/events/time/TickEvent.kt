package tech.thatgravyboat.skyblockapi.api.events.time

import tech.thatgravyboat.skyblockapi.api.events.base.EventBus
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

object TickEvent : SkyBlockEvent() {
    var ticks = 0
        private set

    override fun post(bus: EventBus): Boolean {
        ticks++
        return super.post(bus)
    }
}

