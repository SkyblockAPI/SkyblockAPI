package tech.thatgravyboat.skyblockapi.api.area.mining

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch

@Module
object GlaciteAPI {

    private val glaciteGroup = RegexGroup.SCOREBOARD.group("mining.glacite")

    private val coldRegex = glaciteGroup.create("cold", "Cold: -(?<cold>\\d+)❄")

    var cold: Int = 0
        private set

    @Subscription
    fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
        coldRegex.anyMatch(event.added, "cold") { (cold) ->
            this.cold = cold.toIntValue()
        }
    }
}
