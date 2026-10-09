package tech.thatgravyboat.skyblockapi.api.datetime

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.valueOfOrNull
import tech.thatgravyboat.skyblockapi.api.environmental.DateTimeAPI as NewDateTimeAPI

@Module
@Deprecated("Replace with the environmental Package", ReplaceWith("tech.thatgravyboat.skyblockapi.api.environmental.DateTimeAPI"))
public object DateTimeAPI {
    public val season: SkyBlockSeason? get() = NewDateTimeAPI.season?.let { valueOfOrNull<SkyBlockSeason>(it.name) }
    public val day: Int get() = NewDateTimeAPI.day
    public val hour: Int get() = NewDateTimeAPI.hour
    public val minute: Int get() = NewDateTimeAPI.minute
    public val isDay: Boolean get() = NewDateTimeAPI.isDay
    public val isNight: Boolean get() = NewDateTimeAPI.isNight
}
