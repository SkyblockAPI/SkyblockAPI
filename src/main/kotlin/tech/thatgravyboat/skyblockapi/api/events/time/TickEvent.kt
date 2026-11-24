package tech.thatgravyboat.skyblockapi.api.events.time

import tech.thatgravyboat.skyblockapi.api.events.base.EventBus
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

public object TickEvent : SkyBlockEvent() {
    public var ticks: Int = 0
        private set

    override fun post(bus: EventBus): Boolean {
        ticks++
        return bus.post(this, ticks)
    }
}

