package tech.thatgravyboat.skyblockapi.api.profile.skilltree

import me.owdding.ktcodecs.GenerateCodec

public interface SkillTreeCurrency {
    public val widgetName: String
    public val inventoryName: String get() = widgetName

    public val data: SkillTreeCurrencyData
    public val current: Long get() = data.current
    public val total: Long get() = data.total
}

@GenerateCodec
public data class SkillTreeCurrencyData(
    val current: Long = 0,
    val total: Long = 0,
)
