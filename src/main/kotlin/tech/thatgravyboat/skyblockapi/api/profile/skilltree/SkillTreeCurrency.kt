package tech.thatgravyboat.skyblockapi.api.profile.skilltree

import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.OptionalNullable

interface SkillTreeCurrency {
    val widgetName: String
    val inventoryName: String get() = widgetName

    val data: SkillTreeCurrencyData
    val current: Long get() = data.current
    val total: Long get() = data.total
    val spent: Long get() = data.spent
}

data class SkillTreeCurrencyData(
    val current: Long = 0,
    val total: Long = 0,
) {
    val spent: Long get() = total - current
}

@GenerateCodec
data class SkillCurrencyData<Currency : SkillTreeCurrency>(
    @OptionalNullable
    var currentLoadoutName: String? = null,
    val loadouts: MutableList<SkillTreeCurrencyLoadout<Currency>> = mutableListOf(),
    val total: MutableMap<Currency, Long> = mutableMapOf(),
)

@GenerateCodec
data class SkillTreeCurrencyLoadout<Currency : SkillTreeCurrency>(
    var name: String,
    val currencySpent: MutableMap<Currency, Long> = mutableMapOf(),
)
