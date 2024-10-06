package tech.thatgravyboat.skyblockapi.api.area.dungeon

class DungeonPlayer(
    val name: String,
    dungeonClass: DungeonClass?,
    classLevel: Int?
) {
    var dead: Boolean = false
        internal set

    var dungeonClass: DungeonClass? = dungeonClass
        internal set
    var classLevel: Int? = classLevel
        internal set

    internal fun missingData(): Boolean = dungeonClass == null || classLevel == null
}
