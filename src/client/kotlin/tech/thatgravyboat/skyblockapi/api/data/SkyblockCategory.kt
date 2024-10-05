package tech.thatgravyboat.skyblockapi.api.data

data class SkyblockCategory(val name: String, val isDungeon: Boolean = false) {
    companion object {
        fun create(string: String): SkyblockCategory = if (string.startsWith("dungeon", true)) {
            SkyblockCategory(string.lowercase().removePrefix("dungeon").trim(), true)
        } else {
            SkyblockCategory(string.lowercase().trim())
        }

        val NECKLACE = create("necklace")
        val DUNGEON_NECKLACE = create("dungeon necklace")
        val CLOAK = create("cloak")
        val DUNGEON_CLOAK = create("dungeon cloak")
        val BELT = create("belt")
        val DUNGEON_BELT = create("dungeon belt")
        val GLOVES = create("gloves")
        val DUNGEON_GLOVES = create("dungeon gloves")
        val BRACELET = create("bracelet")
        val DUNGEON_BRACELET = create("dungeon bracelet")
    }
}
