package tech.thatgravyboat.skyblockapi.api.data.stored

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.hotf.HotfData
import tech.thatgravyboat.skyblockapi.api.profile.hotf.HotfPerk
import tech.thatgravyboat.skyblockapi.generated.CodecUtils
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs.getCodec
import java.util.Optional

internal object HotfStorage : SkillTreeStorage<HotfData, HotfPerk>() {

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
                ).apply(it, SkyblockAPICodecs::createHotfDataCodec)
            }

            1 -> SkyblockAPICodecs.HotfDataCodec.codec()
            else -> tech.thatgravyboat.skyblockapi.utils.codecs.CodecUtils.unit { HotfData() }
        }
    }
}

