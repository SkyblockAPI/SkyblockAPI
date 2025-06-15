package tech.thatgravyboat.skyblockapi.api.events.base

import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI

abstract class SkyBlockEvent protected constructor() {

    var isCancelled = false
        private set

    open fun post(bus: EventBus): Boolean =
        bus.post(this)

    @ApiStatus.Internal
    fun `post$sbapi`() = post(SkyBlockAPI.eventBus)
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
