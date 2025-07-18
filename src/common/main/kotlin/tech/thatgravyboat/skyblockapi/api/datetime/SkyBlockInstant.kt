package tech.thatgravyboat.skyblockapi.api.datetime

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.annotations.Range
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private const val EPOCH_DATE = 1559829300000L
private const val YEAR_IN_MILLIS = 446400000L
private const val MONTH_IN_MILLIS = 37200000L
private const val DAY_IN_MILLIS = 1200000L
private const val HOUR_IN_MILLIS = 50000L
private const val MINUTE_IN_MILLIS = 833L
private const val SECOND_IN_MILLIS = 13L

data class SkyBlockInstant(val instant: Instant) {

    constructor(
        year: @Range(from = 0, to = Int.MAX_VALUE.toLong()) Int = 1,
        month: @Range(from = 1, to = 12) Int = 1,
        day: @Range(from = 1, to = 31) Int = 1,
        hour: @Range(from = 0, to = 23) Int = 0,
        minute: @Range(from = 0, to = 59) Int = 0,
        second: @Range(from = 0, to = 59) Int = 0,
    ) : this(
        Instant.fromEpochMilliseconds(
            EPOCH_DATE +
                year * YEAR_IN_MILLIS +
                (month - 1) * MONTH_IN_MILLIS +
                (day - 1) * DAY_IN_MILLIS +
                hour * HOUR_IN_MILLIS +
                minute * MINUTE_IN_MILLIS +
                second * SECOND_IN_MILLIS,
        ),
    )

    val year: Int
        get() = ((instant.toEpochMilliseconds() - EPOCH_DATE) / YEAR_IN_MILLIS).toInt()

    val month: Int
        get() = ((instant.toEpochMilliseconds() - EPOCH_DATE) % YEAR_IN_MILLIS / MONTH_IN_MILLIS).toInt() + 1

    val day: Int
        get() = ((instant.toEpochMilliseconds() - EPOCH_DATE) % MONTH_IN_MILLIS / DAY_IN_MILLIS).toInt() + 1

    val hour: Int
        get() = ((instant.toEpochMilliseconds() - EPOCH_DATE) % DAY_IN_MILLIS / HOUR_IN_MILLIS).toInt()

    val minute: Int
        get() = ((instant.toEpochMilliseconds() - EPOCH_DATE) % HOUR_IN_MILLIS / MINUTE_IN_MILLIS).toInt()

    val second: Int
        get() = ((instant.toEpochMilliseconds() - EPOCH_DATE) % MINUTE_IN_MILLIS / SECOND_IN_MILLIS).toInt()

    operator fun plus(duration: Duration): SkyBlockInstant = SkyBlockInstant(instant.plus(duration))
    operator fun minus(duration: Duration): SkyBlockInstant = SkyBlockInstant(instant.minus(duration))

    operator fun minus(other: SkyBlockInstant): Duration = instant.minus(other.instant)

    fun getSeason(): SkyBlockSeason = SkyBlockSeason.entries[this.month - 1]

    fun copy(
        year: Int = this.year,
        month: Int = this.month,
        day: Int = this.day,
        hour: Int = this.hour,
        minute: Int = this.minute,
        second: Int = this.second,
    ): SkyBlockInstant = SkyBlockInstant(year, month, day, hour, minute, second)

    companion object {

        fun now(): SkyBlockInstant = SkyBlockInstant(Clock.System.now())
    }
}

val Int.skyblockSeconds: Duration get() = (this * SECOND_IN_MILLIS).milliseconds
val Int.skyblockMinutes: Duration get() = (this * MINUTE_IN_MILLIS).milliseconds
val Int.skyblockHours: Duration get() = (this * HOUR_IN_MILLIS).milliseconds
val Int.skyblockDays: Duration get() = (this * DAY_IN_MILLIS).milliseconds
val Int.skyblockMonths: Duration get() = (this * MONTH_IN_MILLIS).milliseconds
val Int.skyblockYears: Duration get() = (this * YEAR_IN_MILLIS).milliseconds
