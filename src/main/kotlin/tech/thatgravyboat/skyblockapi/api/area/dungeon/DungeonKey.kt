package tech.thatgravyboat.skyblockapi.api.area.dungeon

public enum class DungeonKey(private val getter: () -> Int) {
    WITHER(DungeonAPI::witherKeys),
    BLOOD(DungeonAPI::bloodKeys),
    ;

    public val current: Int get() = getter()

    companion object {
        public fun getById(id: String) = entries.firstOrNull { it.name.equals(id, ignoreCase = true) }
    }
}
