package tech.thatgravyboat.skyblockapi.api.area.dungeon

import tech.thatgravyboat.skyblockapi.api.area.isle.kuudra.KuudraTier
import tech.thatgravyboat.skyblockapi.utils.extentions.valueOfOrNull

enum class DungeonFloor(
    val bossName: String,
    val chatBossName: String = bossName,
    val floorNumber: Int,
    val longName: String,
) {
    E("The Watcher", 0, "The Catacombs Entrance"),

    F1("Bonzo", 1, "The Catacombs Floor I"),
    F2("Scarf", 2, "The Catacombs Floor II"),
    F3("The Professor", 3, "The Catacombs Floor III"),
    F4("Thorn", 4, "The Catacombs Floor IV"),
    F5("Livid", 5, "The Catacombs Floor V"),
    F6("Sadan", 6, "The Catacombs Floor VI"),
    F7("Necron", "Maxor", 7, "The Catacombs Floor VII"),

    M1("Bonzo", 1, "Master Mode The Catacombs Floor I"),
    M2("Scarf", 2, "Master Mode The Catacombs Floor II"),
    M3("The Professor", 3, "Master Mode The Catacombs Floor III"),
    M4("Thorn", 4, "Master Mode The Catacombs Floor IV"),
    M5("Livid", 5, "Master Mode The Catacombs Floor V"),
    M6("Sadan", 6, "Master Mode The Catacombs Floor VI"),
    M7("Necron", "Maxor", 7, "Master Mode The Catacombs Floor VII"),
    ;

    constructor(bossName: String, floorNumber: Int, longName: String) : this(bossName, bossName, floorNumber, longName)

    companion object {
        fun getByName(name: String) = valueOfOrNull<KuudraTier>(name.lowercase())
        fun getByLongName(name: String) = DungeonFloor.entries.firstOrNull { it.longName == name }
    }
}
