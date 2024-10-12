package tech.thatgravyboat.skyblockapi.api.area.farming

enum class TrapperAnimalType {
    TRACKABLE,
    UNTRACKABLE,
    UNDETECTED,
    ENDANGERED,
    ELUSIVE,
    UNKNOWN;

    companion object {
        fun fromString(string: String): TrapperAnimalType =
            runCatching { valueOf(string.uppercase()) }.getOrDefault(UNKNOWN)
    }
}
