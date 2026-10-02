package tech.thatgravyboat.skyblockapi.api.profile.skilltree

import me.owdding.ktcodecs.GenerateCodec

interface SkillTreeData<Perk : SkillTreePerk> {
    var currentLoadoutName: String?
    val loadouts: MutableList<SkillTreeLoadout<Perk>>
    var maxTokens: Int
    var tier: Int
}

@GenerateCodec
data class SkillTreeLoadout<Perk : SkillTreePerk>(
    var name: String,
    val perks: MutableMap<String, Perk> = mutableMapOf(),
    var tokensSpent: Int = 0,
)

interface SkillTreePerk {
    val level: Int
    val unlocked: Boolean
    val disabled: Boolean
}
