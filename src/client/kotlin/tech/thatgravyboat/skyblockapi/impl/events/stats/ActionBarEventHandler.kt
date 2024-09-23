package tech.thatgravyboat.skyblockapi.impl.events.stats

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ActionbarReceivedEvent
import tech.thatgravyboat.skyblockapi.modules.Module

@Module
object ActionBarEventHandler {

    private val

    @Subscription
    fun onActionbarReceived(event: ActionbarReceivedEvent) {
        val parts = event.text.split("     ")

    }
}
