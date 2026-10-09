package tech.thatgravyboat.skyblockapi.api.area.mining.events

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.area.mining.MiningEventsAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch

public class GoblinRaidEvent : MiningEvent {

    public var kills: Int = 0
        private set
    public var remaining: Int = 0
        private set

    override val name: String = "Goblin Raid"

    @Module
    public companion object {

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
        internal fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
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
