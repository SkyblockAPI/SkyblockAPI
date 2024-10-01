package tech.thatgravyboat.skyblockapi.api.area.mining.events

import tech.thatgravyboat.skyblockapi.api.area.mining.MiningEventsAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes

data class GoblinRaidEvent(
    var kills: Int = 0,
    var remaining: Int = 0
) : MiningEvent {

    override val name: String = "Goblin Raid"

    @Module
    companion object {

        private val killsRegex = Regexes.create(
            "scoreboard.mining.events.goblinraid.kills",
            "Your kills: (?<kills>[\\d,]+) ☠"
        )

        private val remainingRegex = Regexes.create(
            "scoreboard.mining.events.goblinraid.remaining",
            "Remaining: (?<remaining>[\\d,]+) goblins"
        )

        @Subscription
        fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
            val miningEvent = MiningEventsAPI.event as? GoblinRaidEvent ?: return
            killsRegex.anyMatch(event.added, "kills") { (kills) ->
                miningEvent.kills = kills.toIntValue()
            }
            remainingRegex.anyMatch(event.added, "remaining") { (remaining) ->
                miningEvent.remaining = remaining.toIntValue()
            }
        }
    }
}
