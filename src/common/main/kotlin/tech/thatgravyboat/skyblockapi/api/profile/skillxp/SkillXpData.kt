package tech.thatgravyboat.skyblockapi.api.profile.skillxp

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.HypixelSkillAPI

@GenerateCodec
data class SkillXpData(
    val xp: MutableMap<HypixelSkillAPI.Skill, Float> = mutableMapOf(),
) {
    companion object {
        internal val CODEC = SkyblockAPICodecs.getCodec<SkillXpData>()
    }
}
