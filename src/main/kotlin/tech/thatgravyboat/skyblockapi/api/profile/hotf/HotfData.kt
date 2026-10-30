package tech.thatgravyboat.skyblockapi.api.profile.hotf

import me.owdding.ktcodecs.Compact
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.OptionalNullable
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeData
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeLoadout
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreePerk
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
//~ if >= 26.4 'public' -> 'internal'
internal data class HotfData(
    @OptionalNullable
    override var currentLoadoutName: String? = null,
    @Compact
    override val loadouts: MutableList<SkillTreeLoadout<HotfPerk>> = mutableListOf(),
    override var tier: Int = 0,
    override var maxTokens: Int = 1,
) : SkillTreeData<HotfPerk> {

    //? if <= 26.3 {
    /*@Deprecated("Shouldn't be accessing this", level = HIDDEN)
    public constructor(perks: MutableMap<String, HotfPerk>, tokens: Int = 0, tier: Int = 0) : this()

    @JvmName("component1")
    @Deprecated("Shouldn't be accessing this", level = HIDDEN)
    public fun oldComponent1(): Map<String, HotfPerk> = emptyMap()
    @JvmName("component2")
    @Deprecated("Shouldn't be accessing this", level = HIDDEN)
    public fun oldComponent2(): Int = 0

    @Deprecated("Shouldn't be accessing this", level = HIDDEN)
    public fun copy(perks: MutableMap<String, HotfPerk>, tokens: Int = 0, tier: Int = 0): HotfData = copy()

    @Deprecated("Shouldn't be accessing this", level = HIDDEN)
    override var perks: MutableMap<String, HotfPerk>
        get() = mutableMapOf()
        set(_) {}

    @Deprecated("Shouldn't be accessing this", level = HIDDEN)
    override var tokens: Int
        get() = 0
        set(_) {}
    *///?}

    internal companion object {
        val CODEC = SkyblockAPICodecs.getCodec<HotfData>()
    }
}

@GenerateCodec
public data class HotfPerk(
    override val level: Int,
    override val unlocked: Boolean,
    override val disabled: Boolean,
) : SkillTreePerk
