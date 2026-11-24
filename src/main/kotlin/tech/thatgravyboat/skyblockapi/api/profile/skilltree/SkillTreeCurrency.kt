package tech.thatgravyboat.skyblockapi.api.profile.skilltree

import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.OptionalInt
import me.owdding.ktcodecs.OptionalNullable

public interface SkillTreeCurrency {
    public val widgetName: String
    public val inventoryName: String get() = widgetName

    public val data: SkillTreeCurrencyData
    public val current: Long get() = data.current
    public val total: Long get() = data.total
    public val spent: Long get() = data.spent
}

public data class SkillTreeCurrencyData(
    val current: Long = 0,
    val total: Long = 0,
) {
    val spent: Long get() = total - current
}

@GenerateCodec
public data class SkillCurrencyData<Currency : SkillTreeCurrency>(
    @OptionalNullable
    var currentLoadoutName: String? = null,
    val loadouts: MutableList<SkillTreeCurrencyLoadout<Currency>> = mutableListOf(),
    val total: MutableMap<Currency, Long> = mutableMapOf(),
)

@GenerateCodec
public data class SkillTreeCurrencyLoadout<Currency : SkillTreeCurrency>(
    var name: String,
    @OptionalInt(-1)
    var index: Int = -1,
    val currencySpent: MutableMap<Currency, Long> = mutableMapOf(),
)
