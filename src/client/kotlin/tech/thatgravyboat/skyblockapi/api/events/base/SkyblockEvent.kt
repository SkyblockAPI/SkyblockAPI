package tech.thatgravyboat.skyblockapi.api.events.base

import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI

abstract class SkyblockEvent protected constructor() {

    var isCancelled = false
        private set

    open fun post(bus: EventBus): Boolean =
        bus.post(this)

    internal fun post(): Boolean = post(SkyBlockAPI.eventBus)

    interface Cancellable {

        fun cancel() {
            val event = this as SkyblockEvent
            event.isCancelled = true
        }
    }
}

abstract class CancellableSkyblockEvent :
    SkyblockEvent(),
    SkyblockEvent.Cancellable
