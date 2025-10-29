package tech.thatgravyboat.skyblockapi.api.profile.hotm

import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeCurrency

enum class PowderType(override val widgetName: String) : SkillTreeCurrency {
    MITHRIL("Mithril"),
    GEMSTONE("Gemstone"),
    GLACITE("Glacite"),
    ;
    override val inventoryName: String = "$widgetName Powder"
    override val current: Long get() = PowderAPI.getCurrent(this)
    override val total: Long get() = PowderAPI.getTotal(this)
}
