package tech.thatgravyboat.skyblockapi.api.profile.skilltree

interface SkillTreeData<Perk : SkillTreePerk> {
    var perks: MutableMap<String, Perk>
    var tokens: Int
}

interface SkillTreePerk {
    val level: Int
    val unlocked: Boolean
    val disabled: Boolean
}
