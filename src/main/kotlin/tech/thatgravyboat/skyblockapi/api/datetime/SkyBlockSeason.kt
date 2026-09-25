package tech.thatgravyboat.skyblockapi.api.datetime

import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import tech.thatgravyboat.skyblockapi.utils.extentions.valueOfOrNull

@Deprecated("Replace with the environmental Package", ReplaceWith("tech.thatgravyboat.skyblockapi.api.environmental.SkyBlockSeason"))
enum class SkyBlockSeason {
    EARLY_SPRING,
    SPRING,
    LATE_SPRING,

    EARLY_SUMMER,
    SUMMER,
    LATE_SUMMER,

    EARLY_AUTUMN,
    AUTUMN,
    LATE_AUTUMN,

    EARLY_WINTER,
    WINTER,
    LATE_WINTER,
    ;

    private val string = toFormattedName()

    override fun toString() = string

    companion object {

        fun parse(value: String): SkyBlockSeason? = valueOfOrNull(value.replace(" ", "_").uppercase())
    }
}
