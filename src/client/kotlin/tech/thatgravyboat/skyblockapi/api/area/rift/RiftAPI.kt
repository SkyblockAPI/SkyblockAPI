package tech.thatgravyboat.skyblockapi.api.area.rift

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.RiftTimeActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.profile.CurrencyAPI
import tech.thatgravyboat.skyblockapi.modules.Module
import kotlin.time.Duration

@Module
object RiftAPI {

    var time: Duration? = null
        private set

    val motes: Long
        get() = CurrencyAPI.motes

    @Subscription
    fun onActionBarWidgetChange(event: RiftTimeActionBarWidgetChangeEvent) {
        time = event.time
    }

}
