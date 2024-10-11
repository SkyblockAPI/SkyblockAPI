package tech.thatgravyboat.skyblockapi.api.events.base

import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI

abstract class SkyBlockEvent protected constructor() {

    var isCancelled = false
        private set

    open fun post(bus: EventBus): Boolean =
        bus.post(this)

    internal fun post(): Boolean = post(SkyBlockAPI.eventBus)

    interface Cancellable {

        fun cancel() {
            val event = this as SkyBlockEvent
            event.isCancelled = true
        }
    }
}

abstract class CancellableSkyBlockEvent :
    SkyBlockEvent(),
    SkyBlockEvent.Cancellable
