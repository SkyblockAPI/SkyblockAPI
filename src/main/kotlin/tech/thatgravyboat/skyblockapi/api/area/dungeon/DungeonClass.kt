package tech.thatgravyboat.skyblockapi.api.area.dungeon

import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName

public enum class DungeonClass {
    ARCHER,
    BERSERKER,
    HEALER,
    MAGE,
    TANK,
    ;

    public val displayName: String = toFormattedName()

    public companion object {
        public fun getByName(name: String): DungeonClass? = entries.find { it.displayName == name }
    }
}
