package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.hotx.HotxData
import tech.thatgravyboat.skyblockapi.api.profile.hotx.HotxPerk

internal abstract class HotxStorage<Data : HotxData<Perk>, Perk : HotxPerk> {

    abstract val STORAGE: StoredProfileData<Data>

    var perks: MutableMap<String, Perk>
        get() = STORAGE.get()?.perks ?: mutableMapOf()
        private set(value) {
            STORAGE.get()?.perks = value
            save()
        }

    var tokens: Int
        get() = STORAGE.get()?.tokens ?: 1
        internal set(value) {
            if (this.tokens == value) return
            STORAGE.get()?.tokens = value
            save()
        }

    fun setPerk(name: String, perk: Perk) {
        if (perks[name] == perk) return
        perks[name] = perk
        save()
    }

    fun save() = STORAGE.save()

}
