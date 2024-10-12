package tech.thatgravyboat.skyblockapi.api.area.mining.mineshaft

import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName

enum class CorpseType(val key: String? = null) {
    LAPIS,
    TUNGSTEN("TUNGSTEN_KEY"),
    UMBER("UMBER_KEY"),
    VANGUARD("SKELETON_KEY"),
    ;

    private val string = toFormattedName()
    override fun toString(): String = string

    companion object {
        fun byName(name: String): CorpseType? = entries.find { it.name.equals(name, true) }
    }
}
