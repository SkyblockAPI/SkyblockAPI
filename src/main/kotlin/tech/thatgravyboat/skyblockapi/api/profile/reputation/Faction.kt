package tech.thatgravyboat.skyblockapi.api.profile.reputation

public enum class Faction(public val apiId: String) {
    MAGE("mages"),
    BARBARIAN("barbarians"),
    ;

    public companion object {
        public fun byNameOrNull(name: String): Faction? = entries.firstOrNull { it.name.equals(name, true) }
    }
}
