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

    fun getXp(skill: HypixelSkillAPI.Skill) = SKILL_XP.get()?.xp?.get(skill) ?: 0f
    fun getLevel(skill: HypixelSkillAPI.Skill) = skill.data.getLevelForExp(getXp(skill).toLong())

    fun setXp(skill: HypixelSkillAPI.Skill, xp: Float) {
        SKILL_XP.get()?.xp[skill] = xp
        SKILL_XP.save()
    }

    fun addXp(skill: HypixelSkillAPI.Skill, xp: Float) {
        SKILL_XP.get()?.xp?.merge(skill, xp, Float::plus)
        SKILL_XP.save()
    }

    fun save() {
        SKILL_XP.save()
    }
}
