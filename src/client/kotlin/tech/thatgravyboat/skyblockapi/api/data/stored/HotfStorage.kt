package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.hotf.HotfData
import tech.thatgravyboat.skyblockapi.api.profile.hotf.HotfPerk
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

internal object HotfStorage {

    private val HOTF = StoredProfileData(
        { HotfData() },
        SkyblockAPICodecs.HotfDataCodec.codec(),
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

    var whispersTotal: Long
        get() = HOTF.get()?.whispersTotal ?: 0
        internal set(value) {
            if (this.whispersTotal == value) return
            HOTF.get()?.whispersTotal = value
            save()
        }

    fun setPerk(name: String, perk: HotfPerk) {
        if (perks[name] == perk) return
        perks[name] = perk
        save()
    }

    private fun save() = HOTF.save()

}

