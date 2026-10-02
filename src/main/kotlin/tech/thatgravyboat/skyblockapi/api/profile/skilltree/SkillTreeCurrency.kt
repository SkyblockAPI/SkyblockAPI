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

@GenerateCodec
data class SkillCurrencyData<Currency>(
    var currentLoadoutName: String? = null,
    val loadouts: MutableList<SkillTreeCurrencyLoadout<Currency>> = mutableListOf(),
    val total: MutableMap<Currency, Long> = mutableMapOf(),
) where Currency : SkillTreeCurrency, Currency : Enum<Currency>

@GenerateCodec
data class SkillTreeCurrencyLoadout<Currency>(
    var name: String,
    val currencySpent: MutableMap<Currency, Long> = mutableMapOf(),
) where Currency : SkillTreeCurrency, Currency : Enum<Currency>
