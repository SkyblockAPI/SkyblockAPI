package tech.thatgravyboat.skyblockapi.api.area.mining.events

import tech.thatgravyboat.skyblockapi.api.area.mining.MiningEventsAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch

data class GoblinRaidEvent(
    var kills: Int = 0,
    var remaining: Int = 0
) : MiningEvent {

    override val name: String = "Goblin Raid"

    @Module
    companion object {

        private val regexGroup = RegexGroup.SCOREBOARD.group("mining.events.goblinraid")

        private val killsRegex = regexGroup.create(
            "kills",
            "Your kills: (?<kills>[\\d,]+) ☠"
        )

        private val remainingRegex = regexGroup.create(
            "remaining",
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
