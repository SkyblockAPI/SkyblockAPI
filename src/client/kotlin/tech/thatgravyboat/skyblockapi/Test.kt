package tech.thatgravyboat.skyblockapi

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.modules.Module

@Module
object Test {

    @Subscription
    fun onProfileChange(event: TabWidgetChangeEvent) {

    }
}
