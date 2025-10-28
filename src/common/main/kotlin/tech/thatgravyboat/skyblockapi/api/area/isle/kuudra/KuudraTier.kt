package tech.thatgravyboat.skyblockapi.api.area.isle.kuudra

enum class KuudraTier(val tier: Int) {
    T1(1),
    T2(2),
    T3(3),
    T4(4),
    T5(5);

    companion object {
        fun getByName(name: String) = runCatching { KuudraTier.valueOf(name) }.getOrNull()
        fun getByLongName(name: String) = when (name) {
            "Basic Tier" -> T1
            "Hot Tier" -> T2
            "Burning Tier" -> T3
            "Fiery Tier" -> T4
            "Infernal Tier" -> T5
            else -> null
        }
    }
}
