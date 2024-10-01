package tech.thatgravyboat.skyblockapi.api.area.mining

import tech.thatgravyboat.skyblockapi.api.area.mining.events.GoblinRaidEvent
import tech.thatgravyboat.skyblockapi.api.area.mining.events.MiningEvent
import tech.thatgravyboat.skyblockapi.api.area.mining.events.RaffleMiningEvent
import tech.thatgravyboat.skyblockapi.api.area.mining.events.UnknownMiningEvent
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes

object MiningEventsAPI {

    private val eventsRegex = Regexes.create("scoreboard.mining.events", "Event: (?<event>.+)")

    var event: MiningEvent? = null
        private set

    @Subscription
    fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
        if (event.removed.any { eventsRegex.matches(it) }) {
            this.event = null
        } else if (this.event == null) {
            eventsRegex.anyMatch(event.added, "event") { (event) ->
                this.event = when (event.lowercase()) {
                    "raffle" -> RaffleMiningEvent()
                    "goblin raid" -> GoblinRaidEvent()
                    else -> UnknownMiningEvent(event)
                }
            }
        }
    }
}
