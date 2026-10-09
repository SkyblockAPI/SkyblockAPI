package tech.thatgravyboat.skyblockapi.api.area.farming

import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import tech.thatgravyboat.skyblockapi.utils.extentions.valueOfOrNull

public enum class TrapperAnimalType {
    TRACKABLE,
    UNTRACKABLE,
    UNDETECTED,
    ENDANGERED,
    ELUSIVE,
    UNKNOWN,
    ;

    private val string = toFormattedName()
    override fun toString(): String = string

    public companion object {
        public fun fromString(string: String): TrapperAnimalType = valueOfOrNull(string.uppercase()) ?: UNKNOWN
    }
}
