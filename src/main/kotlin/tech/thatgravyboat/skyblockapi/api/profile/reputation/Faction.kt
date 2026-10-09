package tech.thatgravyboat.skyblockapi.api.profile.reputation

public enum class Faction(val apiId: String) {
    MAGE("mages"),
    BARBARIAN("barbarians"),
    ;

    companion object {
        public fun byNameOrNull(name: String): Faction? = entries.firstOrNull { it.name.equals(name, true) }
    }
}
