package tech.thatgravyboat.skyblockapi.api.profile.skillxp

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.HypixelSkillAPI
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
internal data class SkillExpData(
    val exp: MutableMap<HypixelSkillAPI.Skill, Float> = mutableMapOf(),
) {
    companion object {
        internal val CODEC = SkyblockAPICodecs.getCodec<SkillExpData>()
    }
}
