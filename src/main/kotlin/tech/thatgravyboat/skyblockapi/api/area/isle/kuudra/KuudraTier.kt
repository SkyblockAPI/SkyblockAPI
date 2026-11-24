package tech.thatgravyboat.skyblockapi.api.area.isle.kuudra

import tech.thatgravyboat.skyblockapi.utils.extentions.valueOfOrNull

public enum class KuudraTier(public val tier: Int) {
    BASIC(1),
    HOT(2),
    BURNING(3),
    FIERY(4),
    INFERNAL(5);

    public companion object {
        public fun getByName(name: String): KuudraTier? = valueOfOrNull<KuudraTier>(name.uppercase())
    }
}
