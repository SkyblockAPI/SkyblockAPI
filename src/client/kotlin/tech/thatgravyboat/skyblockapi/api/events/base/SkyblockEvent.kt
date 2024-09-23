package tech.thatgravyboat.skyblockapi.api.events.base

abstract class SkyblockEvent protected constructor() {

    var isCancelled = false
        private set

    fun post(bus: EventBus): Boolean =
        bus.post(this)

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
