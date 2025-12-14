package tech.thatgravyboat.skyblockapi.api.events.hypixel

import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.HypixelSkillAPI

data class SkillXpGainedEvent(
    val skill: HypixelSkillAPI.Skill,
    val amount: Float,
    val currentXp: Float,
) : SkyBlockEvent()
