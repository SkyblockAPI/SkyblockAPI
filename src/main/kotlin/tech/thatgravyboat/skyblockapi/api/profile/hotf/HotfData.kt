package tech.thatgravyboat.skyblockapi.api.profile.hotf

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.api.profile.hotm.PowderType
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeCurrency
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeData
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeLoadout
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreePerk
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec(createCodecMethod = true)
data class HotfData(
    override var currentLoadoutName: String? = null,
    override val loadouts: MutableList<SkillTreeLoadout<HotfPerk>> = mutableListOf(),
    override var maxTokens: Int = 1,
    override var tier: Int = 0,
) : SkillTreeData<HotfPerk> {
    companion object {
        val CODEC = SkyblockAPICodecs.getCodec<HotfData>()
    }
}

@GenerateCodec
data class HotfPerk(
    override val level: Int,
    override val unlocked: Boolean,
    override val disabled: Boolean,
) : SkillTreePerk
