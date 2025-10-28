package tech.thatgravyboat.skyblockapi.api.area.isle.kuudra

enum class KuudraTier(val tier: Int) {
    BASIC(1),
    HOT(2),
    BURNING(3),
    FIERY(4),
    INFERNAL(5);

    companion object {
        fun getByName(name: String) = runCatching { KuudraTier.valueOf(name.uppercase()) }.getOrNull()
    }
}
