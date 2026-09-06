package tech.thatgravyboat.skyblockapi.api.profile.hotf

import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeCurrency

enum class WhisperType(override val widgetName: String) : SkillTreeCurrency {
    FOREST("Forest Whispers"),
    DESERT("Desert Whispers"),
    ;
    override val data get() = WhispersAPI.getData(this)
}
