package tech.thatgravyboat.skyblockapi.api.area.dungeon

enum class DungeonFloor(val displayBossName: String, val chatBossName: String = displayBossName) {
    E("The Watcher"),

    F1("Bonzo"),
    F2("Scarf"),
    F3("The Professor"),
    F4("Thorn"),
    F5("Livid"),
    F6("Sadan"),
    F7("Necron", "Maxor"),

    M1("Bonzo"),
    M2("Scarf"),
    M3("The Professor"),
    M4("Thorn"),
    M5("Livid"),
    M6("Sadan"),
    M7("Necron", "Maxor"),
    ;

    companion object {
        fun getByName(name: String) = runCatching { DungeonFloor.valueOf(name) }.getOrNull()
    }
}
