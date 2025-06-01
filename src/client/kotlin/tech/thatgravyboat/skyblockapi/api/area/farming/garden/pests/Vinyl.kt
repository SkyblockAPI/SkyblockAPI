package tech.thatgravyboat.skyblockapi.api.area.farming.garden.pests

import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName

enum class Vinyl(displayName: String? = null) {
    PRETTY_FLY,
    EARTHWORM_ENSEMBLE,
    CICADA_SYMPHONY,
    BUZZIN_BEATS("Buzzin' Beats"),
    CRICKET_CHOIR,
    RODENT_REVOLUTION,
    DYNAMITES("DynaMITES"),
    BEETLE("Not Just a Pest"),
    SLOW_AND_GROOVY("Slow and Groovy"),
    WINGS_OF_HARMONY("Wings of Harmony"),
    ;

    val displayName: String = displayName ?: toFormattedName()
    val apiId: String = "VINYL_$name"
}
