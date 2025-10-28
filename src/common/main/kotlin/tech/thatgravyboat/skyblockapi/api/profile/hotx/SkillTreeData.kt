package tech.thatgravyboat.skyblockapi.api.profile.hotx

interface SkillTreeData<Perk : SkillTreePerk> {
    var perks: MutableMap<String, Perk>
    val tokens: Int
}

interface SkillTreePerk {
    val level: Int
    val unlocked: Boolean
    val disabled: Boolean
}
