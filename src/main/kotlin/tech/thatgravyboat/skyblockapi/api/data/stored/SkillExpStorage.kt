package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.skillxp.SkillExpData
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.HypixelSkillAPI

internal object SkillExpStorage {
    private val SKILL_EXP = StoredProfileData(
        ::SkillExpData,
        SkillExpData.CODEC,
        "skill.json",
    )

    val data get() = SKILL_EXP.get()

    fun getXp(skill: HypixelSkillAPI.Skill) = SKILL_EXP.get()?.exp?.get(skill) ?: 0f
    fun getLevel(skill: HypixelSkillAPI.Skill) = skill.data.getLevelForExp(getXp(skill).toLong())

    fun setXp(skill: HypixelSkillAPI.Skill, xp: Float) {
        SKILL_EXP.get()?.exp[skill] = xp
        SKILL_EXP.save()
    }

    fun addXp(skill: HypixelSkillAPI.Skill, xp: Float) {
        SKILL_EXP.get()?.exp?.merge(skill, xp, Float::plus)
        SKILL_EXP.save()
    }

    fun save() {
        SKILL_EXP.save()
    }
}
