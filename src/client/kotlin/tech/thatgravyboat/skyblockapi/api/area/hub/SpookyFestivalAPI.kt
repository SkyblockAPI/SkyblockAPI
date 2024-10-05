package tech.thatgravyboat.skyblockapi.api.area.hub

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabListHeaderFooterChangeEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.find
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Module
object SpookyFestivalAPI {

    private val durationRegex = RegexGroup.SCOREBOARD.group("spooky_festival").create(
        "duration",
        "Spooky Festival: (?<min>\\d{1,2}):(?<sec>\\d{2})",
    )

    private val candyRegex = RegexGroup.TABLIST.group("spooky_festival").create(
        "candy",
        "Your Candy: (?<green>[\\d,]+) Green, (?<purple>[\\d,]+) Purple \\((?<points>[\\d,]+) pts.\\)",
    )

    var onGoing: Boolean = false
        private set

    var duration: Duration = Duration.ZERO
        private set

    var greenCandy: Int = 0
        private set

    var purpleCandy: Int = 0
        private set

    var points: Int = 0
        private set

    @Subscription
    fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
        durationRegex.anyMatch(event.added, "min", "sec") { (min, sec) ->
            duration = min.toIntValue().minutes + sec.toIntValue().seconds
        }
    }

    @Subscription
    fun onTabListFooterUpdate(event: TabListHeaderFooterChangeEvent) {
        onGoing = false
        candyRegex.find(event.newFooter.stripped, "green", "purple", "points") { (green, purple, points) ->
            greenCandy = green.toIntValue()
            purpleCandy = purple.toIntValue()
            SpookyFestivalAPI.points = points.toIntValue()
            onGoing = true
        }
    }
}
