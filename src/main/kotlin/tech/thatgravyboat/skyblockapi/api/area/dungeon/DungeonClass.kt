package tech.thatgravyboat.skyblockapi.api.area.dungeon

import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName

enum class DungeonClass {
    ARCHER,
    BERSERKER,
    HEALER,
    MAGE,
    TANK,
    ;

    val displayName = toFormattedName()

    companion object {
        fun getByName(name: String) = entries.find { it.displayName == name }
    }
}
