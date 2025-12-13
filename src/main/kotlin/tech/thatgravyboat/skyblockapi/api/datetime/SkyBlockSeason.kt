package tech.thatgravyboat.skyblockapi.api.datetime

import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName

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

        fun parse(value: String): SkyBlockSeason? = runCatching {
            valueOf(value.replace(" ", "_").uppercase())
        }.getOrNull()
    }
}
