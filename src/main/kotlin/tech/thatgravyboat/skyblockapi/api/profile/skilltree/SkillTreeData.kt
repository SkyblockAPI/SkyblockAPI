package tech.thatgravyboat.skyblockapi.api.profile.skilltree

import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.OptionalInt

public interface SkillTreeData<Perk : SkillTreePerk> {
    public var currentLoadoutName: String?
    public val loadouts: MutableList<SkillTreeLoadout<Perk>>
    public var tier: Int
    public var maxTokens: Int


    //? if <= 26.3 {
    @Deprecated("Shouldn't be accessing this", level = HIDDEN)
    public var perks: MutableMap<String, Perk>

    @Deprecated("Shouldn't be accessing this", level = HIDDEN)
    public var tokens: Int
    //?}

}

@GenerateCodec
public data class SkillTreeLoadout<Perk : SkillTreePerk>(
    var name: String,
    @OptionalInt(-1) var index: Int = -1, // -1 means we don't know the index
    val perks: MutableMap<String, Perk> = mutableMapOf(),
    var tokensSpent: Int = 0,
)

public interface SkillTreePerk {
    public val level: Int
    public val unlocked: Boolean
    public val disabled: Boolean
}
