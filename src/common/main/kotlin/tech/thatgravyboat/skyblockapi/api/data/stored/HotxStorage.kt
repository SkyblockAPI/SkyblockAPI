package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.hotx.SkillTreeData
import tech.thatgravyboat.skyblockapi.api.profile.hotx.SkillTreePerk

internal abstract class HotxStorage<Data : SkillTreeData<Perk>, Perk : SkillTreePerk> {

    abstract val STORAGE: StoredProfileData<Data>

    var perks: MutableMap<String, Perk>
        get() = STORAGE.get()?.perks ?: mutableMapOf()
        private set(value) {
            STORAGE.get()?.perks = value
            save()
        }

    var tokens: Int
        get() = HotfStorage.STORAGE.get()?.tokens ?: 1
        internal set(value) {
            if (this.tokens == value) return
            HotfStorage.STORAGE.get()?.tokens = value
            save()
        }

    fun setPerk(name: String, perk: Perk) {
        if (perks[name] == perk) return
        perks[name] = perk
        save()
    }

    fun save() = STORAGE.save()

}
