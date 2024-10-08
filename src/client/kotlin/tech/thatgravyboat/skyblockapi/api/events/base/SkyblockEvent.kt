package tech.thatgravyboat.skyblockapi.api.events.base

import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI

abstract class SkyblockEvent protected constructor() {

    var isCancelled = false
        private set

    fun post(bus: EventBus): Boolean =
        bus.post(this)

    fun postWithContext(bus: EventBus, context: Any? = null): Boolean =
        bus.post(this, context)

    internal fun post(): Boolean = post(SkyBlockAPI.eventBus)
    internal fun postWithContext(context: Any? = null): Boolean = postWithContext(SkyBlockAPI.eventBus, context)

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
