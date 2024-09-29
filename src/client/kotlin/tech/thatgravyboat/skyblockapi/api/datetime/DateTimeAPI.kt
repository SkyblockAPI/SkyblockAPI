package tech.thatgravyboat.skyblockapi.api.datetime

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes

@Module
object DateTimeAPI {

    private val dateRegex = Regexes.create(
        "scoreboard.date",
        " *(?<season>[A-Za-z ]+) (?<day>\\d+)(st|nd|rd|th)"
    )

    private val timeRegex = Regexes.create(
        "scoreboard.time",
        " *(?<hour>\\d{1,2}):(?<minute>\\d{1,2})(?<period>am|pm) (?<symbol>.)"
    )

    var season: SkyBlockSeason? = null
        private set

    var day: Int = 0
        private set

    var hour: Int = 0
        private set

    var minute: Int = 0
        private set

    val isDay: Boolean
        get() = hour in 6..18

    val isNight: Boolean
        get() = !isDay

    @Subscription
    fun onScoreboardChange(event: ScoreboardUpdateEvent) {
        dateRegex.anyMatch(event.added, "season", "day") { (season, day) ->
            this.season = SkyBlockSeason.parse(season)
            this.day = day.toInt()
        }
        timeRegex.anyMatch(event.added, "hour", "minute", "period") { (hour, minute, period) ->
            this.hour = hour.toIntOrNull() ?: 0
            this.hour = when (this.hour) {
                12 -> if (period == "am") 0 else 12
                else -> this.hour + if (period == "pm") 12 else 0
            }
            this.minute = minute.toIntOrNull() ?: 0
        }
    }
}
