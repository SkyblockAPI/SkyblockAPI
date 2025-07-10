package tech.thatgravyboat.skyblockapi.api.data.stored

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.hotf.HotfData
import tech.thatgravyboat.skyblockapi.api.profile.hotf.HotfPerk
import tech.thatgravyboat.skyblockapi.generated.CodecUtils
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs.getCodec
import java.util.*

internal object HotfStorage {

    private val HOTF = StoredProfileData(
        1,
        ::HotfData,
        "hotf.json",
    ) { version ->


        when (version) {
            0 -> RecordCodecBuilder.create {
                it.group(
                    CodecUtils.map(getCodec<String>(), getCodec<HotfPerk>()).optionalFieldOf("perks").forGetter { getter -> Optional.of(getter.perks) },
                    getCodec<Int>().optionalFieldOf("tokens").forGetter { getter -> Optional.of(getter.tokens) },
                    getCodec<Long>().optionalFieldOf("whispers").forGetter { getter -> Optional.of(getter.forest) },
                    getCodec<Long>().optionalFieldOf("whispersTotal").forGetter { getter -> Optional.of(getter.forestTotal) },
                ).apply(it, SkyblockAPICodecs::createHotfDataCodec)
            }

            1 -> SkyblockAPICodecs.HotfDataCodec.codec()
            else -> Codec.unit { HotfData() }
        }
    }

    val perks: MutableMap<String, HotfPerk>
        get() = HOTF.get()?.perks ?: mutableMapOf()

    var tokens: Int
        get() = HOTF.get()?.tokens ?: 1
        internal set(value) {
            if (this.tokens == value) return
            HOTF.get()?.tokens = value
            save()
        }

    var forest: Long
        get() = HOTF.get()?.forest ?: 0
        internal set(value) {
            if (this.forest == value) return
            HOTF.get()?.forest = value
            save()
        }

    var forestTotal: Long
        get() = HOTF.get()?.forestTotal ?: 0
        internal set(value) {
            if (this.forestTotal == value) return
            HOTF.get()?.forestTotal = value
            save()
        }

    fun setPerk(name: String, perk: HotfPerk) {
        if (perks[name] == perk) return
        perks[name] = perk
        save()
    }

    private fun save() = HOTF.save()

}

