package tech.thatgravyboat.skyblockapi.api.area.mining

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes

@Module
object GlaciteAPI {

    private val coldRegex = Regexes.create("scoreboard.mining.glacite.cold", "Cold: -(?<cold>\\d+)❄")

    var cold: Int = 0
        private set

    @Subscription
    fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
        coldRegex.anyMatch(event.added, "cold") { (cold) ->
            this.cold = cold.toIntValue()
        }
    }
}
