package tech.thatgravyboat.skyblockapi.api.area.dungeon

enum class DungeonKey(private val getter: () -> Int) {
    WITHER(DungeonAPI::witherKeys),
    BLOOD(DungeonAPI::bloodKeys),
    ;

    val current: Int get() = getter()

    companion object {
        fun getById(id: String) = entries.firstOrNull { it.name.equals(id, ignoreCase = true) }
    }
}
