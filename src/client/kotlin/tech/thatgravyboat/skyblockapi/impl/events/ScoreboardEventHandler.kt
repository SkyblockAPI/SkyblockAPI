package tech.thatgravyboat.skyblockapi.impl.events

import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardTitleUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.modules.Module

private const val CHECK_INTERVAL = 1000

@Module
object ScoreboardEventHandler {

    private var lastCheck = 0L

    private var scoreboard = listOf<String>()
    private var currentTitle: String? = null

    @Subscription
    fun onTick(event: TickEvent) {
        if (!LocationAPI.isOnSkyblock) return
        if (System.currentTimeMillis() - lastCheck < CHECK_INTERVAL) return
        lastCheck = System.currentTimeMillis()

        handleScoreboard()
        handleTitle()
    }

    private fun handleScoreboard() {
        val new = McClient.scoreboard?.toList() ?: return
        val old = scoreboard
        if (new == old) return
        ScoreboardUpdateEvent(old, new).post(SkyBlockAPI.eventBus)
        scoreboard = new.toMutableList()
    }

    private fun handleTitle() {
        val newTitle = McClient.scoreboardTitle
        if (newTitle != null && newTitle != currentTitle) {
            ScoreboardTitleUpdateEvent(currentTitle, newTitle).post(SkyBlockAPI.eventBus)
            currentTitle = newTitle
        }
    }
}
