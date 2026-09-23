package tech.thatgravyboat.skyblockapi.api.datetime

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.environmental.DateTimeAPI as NewDateTimeAPI

@Module
@Deprecated("Replace with the environmental Package", ReplaceWith("tech.thatgravyboat.skyblockapi.api.environmental.DateTimeAPI"))
object DateTimeAPI {
    val season: SkyBlockSeason? get() = NewDateTimeAPI.season?.let { SkyBlockSeason.valueOf(it.name) }
    val day: Int get() = NewDateTimeAPI.day
    val hour: Int get() = NewDateTimeAPI.hour
    val minute: Int get() = NewDateTimeAPI.minute
    val isDay: Boolean get() = NewDateTimeAPI.isDay
    val isNight: Boolean get() = NewDateTimeAPI.isNight
}
