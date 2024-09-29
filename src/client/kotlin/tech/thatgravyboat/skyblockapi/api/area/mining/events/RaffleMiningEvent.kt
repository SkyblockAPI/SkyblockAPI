package tech.thatgravyboat.skyblockapi.api.area.mining.events

import tech.thatgravyboat.skyblockapi.api.area.mining.MiningEventsAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes

data class RaffleMiningEvent(
    var tickets: Int = 0,
    var pool: Int = 0
) : MiningEvent {

    override val name: String = "Raffle"

    @Module
    companion object {

        private val ticketsRegex = Regexes.create(
            "scoreboard.mining.events.raffle.tickets",
            "Tickets: (?<tickets>\\d+) \\((?<percent>[\\d.]+)%\\)"
        )
        private val poolRegex = Regexes.create(
            "scoreboard.mining.events.raffle.pool",
            "Pool: (?<pool>\\d+)"
        )

        @Subscription
        fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
            val miningEvent = MiningEventsAPI.event as? RaffleMiningEvent ?: return
            ticketsRegex.anyMatch(event.added, "tickets") { (tickets) ->
                miningEvent.tickets = tickets.toIntValue()
            }
            poolRegex.anyMatch(event.added, "pool") { (pool) ->
                miningEvent.pool = pool.toIntValue()
            }
        }
    }
}
