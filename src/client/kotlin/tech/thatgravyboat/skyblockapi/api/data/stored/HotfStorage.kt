package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.hotf.HotfData
import tech.thatgravyboat.skyblockapi.api.profile.hotf.HotfPerk

internal object HotfStorage {

    private val HOTF = StoredProfileData(
        { HotfData() },
        HotfData.CODEC,
        "hotf.json",
    )

    val perks: MutableMap<String, HotfPerk>
        get() = HOTF.get()?.perks ?: mutableMapOf()

    var tokens: Int
        get() = HOTF.get()?.tokens ?: 1
        internal set(value) {
            if (this.tokens == value) return
            HOTF.get()?.tokens = value
            save()
        }

    var whispers: Long
        get() = HOTF.get()?.whispers ?: 0
        internal set(value) {
            if (this.whispers == value) return
            HOTF.get()?.whispers = value
            save()
        }

    fun setPerk(name: String, perk: HotfPerk) {
        if (perks[name] == perk) return
        perks[name] = perk
        save()
    }

    private fun save() = HOTF.save()

}

