package tech.thatgravyboat.skyblockapi.api.profile.skilltree

import me.owdding.ktcodecs.GenerateCodec

interface SkillTreeCurrency {
    val widgetName: String
    val inventoryName: String get() = widgetName

    val current: Long
    val total: Long
}

@GenerateCodec
data class SkillTreeCurrencyData(
    val current: Long = 0,
    val total: Long = 0,
)
