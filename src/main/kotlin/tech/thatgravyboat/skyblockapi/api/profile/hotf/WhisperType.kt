package tech.thatgravyboat.skyblockapi.api.profile.hotf

import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeCurrency
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeCurrencyData

public enum class WhisperType(override val widgetName: String) : SkillTreeCurrency {
    FOREST("Forest Whispers"),
    DESERT("Desert Whispers"),
    ;
    override val data: SkillTreeCurrencyData get() = WhispersAPI.getData(this)
}
