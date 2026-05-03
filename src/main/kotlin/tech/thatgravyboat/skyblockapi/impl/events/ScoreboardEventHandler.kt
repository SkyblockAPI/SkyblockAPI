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

    private var lastStrippedScoreboard = listOf<String>()
    private var lastScoreboard = listOf<Component>()
    private var lastTitle: String? = null

    @OnlyOnSkyBlock
    @TimePassed("1s")
    @Subscription(TickEvent::class)
    fun onTick() {
        if (!ProfileAPI.isLoaded) return

        handleScoreboard(McClient.scoreboard.toList())
        handleTitle()
    }

    @Subscription(ServerChangeEvent::class)
    fun onServerSwitch() = handleScoreboard(emptyList())

    private fun handleScoreboard(new: List<Component>) {
        val newStripped = new.map { it.stripped }
        if (lastScoreboard == new) return
        ScoreboardUpdateEvent(lastStrippedScoreboard, newStripped, lastScoreboard, new).post()
        lastStrippedScoreboard = newStripped
        lastScoreboard = new
    }

    private fun handleTitle() {
        val newTitle = McClient.scoreboardTitle?.stripped
        if (newTitle != null && newTitle != lastTitle) {
            ScoreboardTitleUpdateEvent(lastTitle, newTitle).post(SkyBlockAPI.eventBus)
            lastTitle = newTitle
        }
    }
}
