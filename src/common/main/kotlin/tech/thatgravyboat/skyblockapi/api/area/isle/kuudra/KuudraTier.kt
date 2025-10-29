package tech.thatgravyboat.skyblockapi.api.area.isle.kuudra

import tech.thatgravyboat.skyblockapi.utils.extentions.valueOfOrNull

enum class KuudraTier(val tier: Int) {
    BASIC(1),
    HOT(2),
    BURNING(3),
    FIERY(4),
    INFERNAL(5);

    companion object {
        fun getByName(name: String) = valueOfOrNull<KuudraTier>(name.lowercase())
    }
}
