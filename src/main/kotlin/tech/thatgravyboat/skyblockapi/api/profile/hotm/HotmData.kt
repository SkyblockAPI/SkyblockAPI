package tech.thatgravyboat.skyblockapi.api.profile.hotm

import me.owdding.ktcodecs.Compact
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.OptionalNullable
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeData
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeLoadout
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreePerk

@GenerateCodec
data class HotmData(
    @OptionalNullable
    override var currentLoadoutName: String? = null,
    @Compact
    override val loadouts: MutableList<SkillTreeLoadout<HotmPerk>> = mutableListOf(),
    override var maxTokens: Int = 1,
    override var tier: Int = 0,
) : SkillTreeData<HotmPerk>

@GenerateCodec
data class HotmPerk(
    override val level: Int,
    override val unlocked: Boolean,
    override val disabled: Boolean,
) : SkillTreePerk
