package tech.thatgravyboat.skyblockapi.api.data

data class SkyBlockCategory(val name: String, val isDungeon: Boolean = false) {
    @Suppress("unused")
    companion object {
        private val registeredCategories = mutableMapOf<String, SkyBlockCategory>()

        fun create(string: String): SkyBlockCategory {
            val formatted = string.lowercase()
            return registeredCategories.getOrPut(formatted) {
                if (formatted.startsWith("dungeon", true)) {
                    SkyBlockCategory(formatted.removePrefix("dungeon").trim(), true)
                } else {
                    SkyBlockCategory(formatted.trim())
                }
            }
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
        val ARROW = create("arrow")
    }
}
