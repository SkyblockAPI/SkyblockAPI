package tech.thatgravyboat.skyblockapi.api.profile.hotf

import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeCurrency

enum class WhisperType(override val widgetName: String) : SkillTreeCurrency {
    FOREST("Forest Whispers"),
    ;
    override val current: Long get() = WhispersAPI.getCurrent(this)
    override val total: Long get() = WhispersAPI.getTotal(this)
}
