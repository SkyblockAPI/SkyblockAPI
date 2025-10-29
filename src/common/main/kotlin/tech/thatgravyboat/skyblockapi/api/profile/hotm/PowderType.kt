package tech.thatgravyboat.skyblockapi.api.profile.hotm

import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeCurrency
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeCurrencyData

enum class PowderType(override val widgetName: String) : SkillTreeCurrency {
    MITHRIL("Mithril"),
    GEMSTONE("Gemstone"),
    GLACITE("Glacite"),
    ;
    override val inventoryName: String = "$widgetName Powder"
    override val data: SkillTreeCurrencyData get() = PowderAPI.getData(this)
}
