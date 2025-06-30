package tech.thatgravyboat.skyblockapi.api.profile.items.museum

import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName

enum class MuseumCategory {
    WEAPONS,
    ARMOR_SETS,
    RARITIES,
    SPECIAL_ITEMS,
    ;

    inline val isArmor: Boolean get() = this == ARMOR_SETS
    inline val isSpecial: Boolean get() = this == SPECIAL_ITEMS
    private val displayName = toFormattedName()
    override fun toString(): String = displayName

    companion object {
        fun fromName(name: String): MuseumCategory? = entries.find { it.displayName.equals(name, ignoreCase = true) }
    }
}
