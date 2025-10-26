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

internal object HotfStorage : HotxStorage<HotfData, HotfPerk>() {

    override val STORAGE = StoredProfileData(
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

    var forest: Long
        get() = STORAGE.get()?.forest ?: 0
        internal set(value) {
            if (this.forest == value) return
            STORAGE.get()?.forest = value
            save()
        }

    var forestTotal: Long
        get() = STORAGE.get()?.forestTotal ?: 0
        internal set(value) {
            if (this.forestTotal == value) return
            STORAGE.get()?.forestTotal = value
            save()
        }
}

