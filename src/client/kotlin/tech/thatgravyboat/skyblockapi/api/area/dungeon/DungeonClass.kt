package tech.thatgravyboat.skyblockapi.api.area.dungeon

enum class DungeonClass(val displayName: String) {
    ARCHER("Archer"),
    BERSERKER("Berserker"),
    HEALER("Healer"),
    MAGE("Mage"),
    TANK("Tank"),
    ;

    companion object {
        fun getByName(name: String) = entries.firstOrNull { it.displayName == name }
    }
}
