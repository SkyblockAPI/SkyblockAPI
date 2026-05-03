package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeData
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreePerk

internal abstract class SkillTreeStorage<Data : SkillTreeData<Perk>, Perk : SkillTreePerk> {

    protected abstract val storage: StoredProfileData<Data>

    val perks: Map<String, Perk>
        get() = storage.get()?.perks ?: mutableMapOf()

    var tokens: Int
        get() = storage.get()?.tokens ?: 1
        internal set(value) = storage.edit {
            if (tokens == value) return
            tokens = value
        }

    val tier: Int
        get() = storage.get()?.tier ?: 0

    fun setMinTier(minTier: Int) {
        storage.edit {
            if (tier >= minTier) return
            tier = minTier
        }
    }

    fun setPerk(name: String, perk: Perk) {
        storage.edit {
            if (perks[name] == perk) return
            perks[name] = perk
        }
    }

    fun save() = storage.save()

}
