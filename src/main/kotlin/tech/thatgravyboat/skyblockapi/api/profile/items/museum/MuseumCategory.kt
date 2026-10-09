package tech.thatgravyboat.skyblockapi.api.profile.items.museum

import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName

enum class MuseumCategory(internal val deprecated: Boolean = false) {
    COMBAT,
    FARMING,
    MINING,
    FISHING,
    FORAGING,
    DUNGEONEERING,
    HUNTING,
    SPECIAL_ITEMS,
    ;

    inline val isSpecial: Boolean get() = this == SPECIAL_ITEMS
    private val displayName = toFormattedName()
    override fun toString(): String = displayName

    companion object {
        fun fromName(name: String): MuseumCategory? = entries.find { !it.deprecated && it.displayName.equals(name, ignoreCase = true) }
    }
}
