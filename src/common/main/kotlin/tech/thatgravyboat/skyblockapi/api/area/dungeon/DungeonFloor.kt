package tech.thatgravyboat.skyblockapi.api.area.dungeon

enum class DungeonFloor(
    val bossName: String,
    val chatBossName: String = bossName,
    val floorNumber: Int,
) {
    E("The Watcher", 0),

    F1("Bonzo", 1),
    F2("Scarf", 2),
    F3("The Professor", 3),
    F4("Thorn", 4),
    F5("Livid", 5),
    F6("Sadan", 6),
    F7("Necron", "Maxor", 7),

    M1("Bonzo", 1),
    M2("Scarf", 2),
    M3("The Professor", 3),
    M4("Thorn", 4),
    M5("Livid", 5),
    M6("Sadan", 6),
    M7("Necron", "Maxor", 7),
    ;

    constructor(bossName: String, floorNumber: Int) : this(bossName, bossName, floorNumber)

    companion object {
        fun getByName(name: String) = runCatching { DungeonFloor.valueOf(name) }.getOrNull()
        fun getByLongName(name: String) = when (name) {
            "The Catacombs Entrance" -> E
            "The Catacombs Floor I" -> F1
            "The Catacombs Floor II" -> F2
            "The Catacombs Floor III" -> F3
            "The Catacombs Floor IV" -> F4
            "The Catacombs Floor V" -> F5
            "The Catacombs Floor VI" -> F6
            "The Catacombs Floor VII" -> F7
            "Master Mode The Catacombs Floor I" -> M1
            "Master Mode The Catacombs Floor II" -> M2
            "Master Mode The Catacombs Floor III" -> M3
            "Master Mode The Catacombs Floor IV" -> M4
            "Master Mode The Catacombs Floor V" -> M5
            "Master Mode The Catacombs Floor VI" -> M6
            "Master Mode The Catacombs Floor VII" -> M7
            else -> null
        }
    }
}
