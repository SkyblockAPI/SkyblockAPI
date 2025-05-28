package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.skillxp.SkillXpData
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.HypixelSkillAPI

internal object SkillXpStorage {
    private val SKILL_XP = StoredProfileData(
        ::SkillXpData,
        SkillXpData.CODEC,
        "skill.json",
    )

    val data get() = SKILL_XP.get()

    fun setXp(skill: HypixelSkillAPI.Skill, xp: Long) {
        SKILL_XP.get()?.xp[skill] = xp
        SKILL_XP.save()
    }

    fun addXp(skill: HypixelSkillAPI.Skill, xp: Long) {
        SKILL_XP.get()?.xp?.merge(skill, xp, Long::plus)
        SKILL_XP.save()
    }
}
