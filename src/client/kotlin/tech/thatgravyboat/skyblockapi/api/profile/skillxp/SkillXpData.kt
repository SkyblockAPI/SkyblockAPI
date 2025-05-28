package tech.thatgravyboat.skyblockapi.api.profile.skillxp

import tech.thatgravyboat.skyblockapi.api.remote.hypixel.HypixelSkillAPI
import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec

@GenerateCodec
data class SkillXpData(
    val xp: MutableMap<HypixelSkillAPI.Skill, Long> = mutableMapOf(),
) {
    companion object {
        internal val CODEC = KCodec.getCodec<SkillXpData>()
    }
}
