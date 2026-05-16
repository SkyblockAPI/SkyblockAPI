package tech.thatgravyboat.skyblockapi.api.profile.reputation

enum class Faction(val apiId: String) {
    MAGE("mages"),
    BARBARIAN("barbarians"),
    ;

    companion object {
        fun byNameOrNull(name: String): Faction? = entries.firstOrNull { it.name.equals(name, true) }
    }
}
