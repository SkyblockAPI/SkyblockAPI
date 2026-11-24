package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.hotf.HotfData
import tech.thatgravyboat.skyblockapi.api.profile.hotf.HotfPerk
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

internal object HotfStorage : SkillTreeStorage<HotfPerk, HotfData>() {

    override val storage = StoredProfileData<HotfData>(
        file = "hotf.json",
        version = 2,
    ) { version ->
        when (version) {
            2 -> SkyblockAPICodecs.HotfDataCodec.codec()
            else -> tech.thatgravyboat.skyblockapi.utils.codecs.CodecUtils.unit { HotfData() }
        }
    }
}

