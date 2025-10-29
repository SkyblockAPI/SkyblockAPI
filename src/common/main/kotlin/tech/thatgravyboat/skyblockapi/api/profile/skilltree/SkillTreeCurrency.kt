package tech.thatgravyboat.skyblockapi.api.profile.skilltree

import me.owdding.ktcodecs.GenerateCodec

interface SkillTreeCurrency {
    val widgetName: String
    val inventoryName: String get() = widgetName

    val data: SkillTreeCurrencyData
    val current: Long get() = data.current
    val total: Long get() = data.total
}

@GenerateCodec
data class SkillTreeCurrencyData(
    val current: Long = 0,
    val total: Long = 0,
)
