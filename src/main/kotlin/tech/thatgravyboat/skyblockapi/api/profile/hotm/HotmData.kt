package tech.thatgravyboat.skyblockapi.api.profile.hotm

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeData
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreePerk
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
public data class HotmData(
    override var perks: MutableMap<String, HotmPerk> = mutableMapOf(),
    override var tokens: Int = 0,
    override var tier: Int = 0,
) : SkillTreeData<HotmPerk> {
    companion object {
        public val CODEC = SkyblockAPICodecs.getCodec<HotmData>()
    }
}

@GenerateCodec
public data class HotmPerk(
    override val level: Int,
    override val unlocked: Boolean,
    override val disabled: Boolean,
) : SkillTreePerk
