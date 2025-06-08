package tech.thatgravyboat.skyblockapi.api.data.stored

import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.hotf.HotfPerk
import tech.thatgravyboat.skyblockapi.generated.CodecUtils
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

internal object HotfStorage {

    private val HOTF = StoredProfileData(
        { mutableMapOf() },
        CodecUtils.map(Codec.STRING, SkyblockAPICodecs.HotfPerkCodec.codec()),
        "hotf.json",
    )

    var perks: MutableMap<String, HotfPerk>
        get() = HOTF.get() ?: mutableMapOf()
        private set(value) {
            HOTF.get().apply {
                this?.clear()
                this?.putAll(value)
            }
            HOTF.save()
        }

    fun setPerk(name: String, perk: HotfPerk) {
        if (perks[name] == perk) return
        perks[name] = perk
        HOTF.save()
    }

}

