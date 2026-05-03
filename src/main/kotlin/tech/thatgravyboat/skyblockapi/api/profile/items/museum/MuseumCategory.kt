package tech.thatgravyboat.skyblockapi.api.profile.items.museum

import tech.thatgravyboat.skyblockapi.RemoveNextVersion
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
    @RemoveNextVersion @Deprecated("This museum category doesn't exist anymore!", level = DeprecationLevel.ERROR)
    WEAPONS(true),
    @RemoveNextVersion @Deprecated("This museum category doesn't exist anymore!", level = DeprecationLevel.ERROR)
    ARMOR_SETS(true),
    @RemoveNextVersion @Deprecated("This museum category doesn't exist anymore!", level = DeprecationLevel.ERROR)
    RARITIES(true),
    ;

    @RemoveNextVersion @Deprecated("This museum category doesn't exist anymore!", level = DeprecationLevel.ERROR)
    inline val isArmor: Boolean get() = false
    inline val isSpecial: Boolean get() = this == SPECIAL_ITEMS
    private val displayName = toFormattedName()
    override fun toString(): String = displayName

    companion object {
        fun fromName(name: String): MuseumCategory? = entries.find { !it.deprecated && it.displayName.equals(name, ignoreCase = true) }
    }
}
