package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeData
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeLoadout
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreePerk

internal abstract class SkillTreeStorage<
    Perk : SkillTreePerk,
    Data : SkillTreeData<Perk>,
> {
    protected abstract val storage: StoredProfileData<Data>

    fun getLoadoutByName(name: String?): SkillTreeLoadout<Perk>? {
        if (name == null) return null
        val data = storage.get() ?: return null

        val current = data.loadouts.find { it.name == name }
        if (current != null) return current

        val new = SkillTreeLoadout<Perk>(name)
        data.loadouts.add(new)
        save()

        return new
    }

    val currentLoadout: SkillTreeLoadout<Perk>?
        get() = getLoadoutByName(currentPreset)

    val perks: Map<String, Perk>
        get() = currentLoadout?.perks ?: emptyMap()

    var currentPreset: String?
        get() = storage.get()?.currentLoadoutName
        set(value) = storage.edit {
            if (currentLoadoutName == value) return
            currentLoadoutName = value
        }

    var maxTokens: Int
        get() = storage.get()?.maxTokens ?: 1
        internal set(value) = storage.edit {
            if (maxTokens == value) return
            maxTokens = value
        }

    var spentTokens: Int
        get() = currentLoadout?.tokensSpent ?: 0
        internal set(value) {
            val currentLoadout = currentLoadout ?: return
            if (currentLoadout.tokensSpent == value) return
        }

    val tokens: Int
        get() = maxTokens - spentTokens

    val tier: Int
        get() = storage.get()?.tier ?: 0

    fun setMinTier(minTier: Int) {
        storage.edit {
            if (tier >= minTier) return
            tier = minTier
        }
    }

    fun setPerk(name: String, perk: Perk) {
        val loadout = currentLoadout ?: return
        if (loadout.perks[name] == perk) return

        loadout.perks[name] = perk
        save()
    }

    fun save() = storage.save()

}
