package tech.thatgravyboat.skyblockapi.api.profile.items.museum

import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName

enum class MuseumCategory(internal val deprecated: Boolean = false) {
public enum class MuseumCategory(internal val deprecated: Boolean = false) {
    COMBAT,
    FARMING,
    MINING,
    FISHING,
    FORAGING,
    DUNGEONEERING,
    HUNTING,
    SPECIAL_ITEMS,
    ;

    public inline val isSpecial: Boolean get() = this == SPECIAL_ITEMS
    private val displayName = toFormattedName()
    override fun toString(): String = displayName

    public companion object {
        public fun fromName(name: String): MuseumCategory? = entries.find { !it.deprecated && it.displayName.equals(name, ignoreCase = true) }
    }
}
