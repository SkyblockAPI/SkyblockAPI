package tech.thatgravyboat.skyblockapi.api.profile.hotf

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.api.profile.hotx.SkillTreeData
import tech.thatgravyboat.skyblockapi.api.profile.hotx.SkillTreePerk

@GenerateCodec(createCodecMethod = true)
data class HotfData(
    override var perks: MutableMap<String, HotfPerk> = mutableMapOf(),
    override var tokens: Int = 0,
    var forest: Long = 0,
    var forestTotal: Long = 0,
) : SkillTreeData<HotfPerk>

@GenerateCodec
data class HotfPerk(
    override val level: Int,
    override val unlocked: Boolean,
    override val disabled: Boolean,
) : SkillTreePerk
