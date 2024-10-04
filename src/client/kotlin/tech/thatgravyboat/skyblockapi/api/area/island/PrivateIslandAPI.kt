package tech.thatgravyboat.skyblockapi.api.area.island

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.parseColonDuration
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes
import kotlin.time.Duration

@Module
object PrivateIslandAPI {

    private val flightDurationRegex = Regexes.create(
        "scoreboard.private_island.flight_duration",
        "Flight Duration: (?<duration>[\\d:]+)"
    )

    var flightDuration: Duration = Duration.ZERO
        private set

    @Subscription
    fun onScoreboardUpdate(event: tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent) {
        flightDurationRegex.anyMatch(event.added, "duration") { (duration) ->
            flightDuration = duration.parseColonDuration() ?: Duration.ZERO
        }
    }
}
