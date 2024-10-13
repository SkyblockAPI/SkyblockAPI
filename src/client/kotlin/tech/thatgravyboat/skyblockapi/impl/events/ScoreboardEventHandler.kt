package tech.thatgravyboat.skyblockapi.impl.events

import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardTitleUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

private const val CHECK_INTERVAL = 1000

@Module
object ScoreboardEventHandler {

    private var lastCheck = 0L

    private var scoreboard = listOf<String>()
    private var currentTitle: String? = null

    @Subscription
    fun onTick(event: TickEvent) {
        if (!LocationAPI.isOnSkyBlock) return
        if (System.currentTimeMillis() - lastCheck < CHECK_INTERVAL) return
        if (!ProfileAPI.isLoaded) return
        lastCheck = System.currentTimeMillis()

        handleScoreboard()
        handleTitle()
    }

    private fun handleScoreboard() {
        val new = McClient.scoreboard
        val newAsText = new.map { it.stripped }
        if (newAsText == scoreboard) return
        ScoreboardUpdateEvent(scoreboard, newAsText, new.toList()).post(SkyBlockAPI.eventBus)
        scoreboard = newAsText.toMutableList()
    }

    private fun handleTitle() {
        val newTitle = McClient.scoreboardTitle?.stripped
        if (newTitle != null && newTitle != currentTitle) {
            ScoreboardTitleUpdateEvent(currentTitle, newTitle).post(SkyBlockAPI.eventBus)
            currentTitle = newTitle
        }
    }
}
