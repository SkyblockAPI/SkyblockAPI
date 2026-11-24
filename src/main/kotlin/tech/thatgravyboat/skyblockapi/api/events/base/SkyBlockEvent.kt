package tech.thatgravyboat.skyblockapi.api.events.base

import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI

public abstract class SkyBlockEvent protected constructor() {

    public var isCancelled: Boolean = false
        private set

    public open fun post(bus: EventBus): Boolean =
        bus.post(this)

    internal fun post(): Boolean = post(SkyBlockAPI.eventBus)

    public interface Cancellable {

        public fun cancel() {
            val event = this as SkyBlockEvent
            event.isCancelled = true
        }
    }
}

public abstract class CancellableSkyBlockEvent : SkyBlockEvent(), SkyBlockEvent.Cancellable
