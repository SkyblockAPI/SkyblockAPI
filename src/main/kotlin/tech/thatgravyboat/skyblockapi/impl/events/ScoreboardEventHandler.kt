package tech.thatgravyboat.skyblockapi.impl.events

import me.owdding.ktmodules.Module
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardTitleUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@Module
object ScoreboardEventHandler {

    private var scoreboardStripped = listOf<String>()
    private var scoreboard = listOf<Component>()
    private var currentTitle: String? = null

    @Subscription
    @OnlyOnSkyBlock
    @TimePassed("1s")
    fun onTick(event: TickEvent) {
        if (!ProfileAPI.isLoaded) return

        handleScoreboard()
        handleTitle()
    }

    @Subscription(ServerChangeEvent::class)
    fun onServerSwitch() {
        ScoreboardUpdateEvent(scoreboardStripped, emptyList(), scoreboard, emptyList()).post()
        scoreboardStripped = emptyList()
        scoreboard = emptyList()
    }

    private fun handleScoreboard() {
        val new = McClient.scoreboard
        val newStripped = new.map { it.stripped }
        if (newStripped == scoreboardStripped && new == scoreboard) return // If nothing changed, not even the colors
        ScoreboardUpdateEvent(scoreboardStripped, newStripped, scoreboard, new.toList()).post(SkyBlockAPI.eventBus)
        scoreboardStripped = newStripped
        scoreboard = new.toList()
    }

    private fun handleTitle() {
        val newTitle = McClient.scoreboardTitle?.stripped
        if (newTitle != null && newTitle != currentTitle) {
            ScoreboardTitleUpdateEvent(currentTitle, newTitle).post(SkyBlockAPI.eventBus)
            currentTitle = newTitle
        }
    }
}
