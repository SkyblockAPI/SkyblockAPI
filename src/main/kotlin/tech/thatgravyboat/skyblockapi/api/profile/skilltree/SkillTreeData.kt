package tech.thatgravyboat.skyblockapi.api.profile.skilltree

public interface SkillTreeData<Perk : SkillTreePerk> {
    public var perks: MutableMap<String, Perk>
    public var tokens: Int
    public var tier: Int
}

public interface SkillTreePerk {
    public val level: Int
    public val unlocked: Boolean
    public val disabled: Boolean
}
