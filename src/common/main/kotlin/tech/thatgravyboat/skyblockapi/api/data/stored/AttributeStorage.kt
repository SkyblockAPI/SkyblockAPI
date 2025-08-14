package tech.thatgravyboat.skyblockapi.api.data.stored

import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.hunting.AttributeAPI.attributeLevelData
import tech.thatgravyboat.skyblockapi.api.profile.hunting.AttributeData
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.generated.CodecUtils
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@Module
internal object AttributeStorage {
    private val ATTRIBUTE_DATA = StoredProfileData(
        { mutableMapOf() },
        CodecUtils.map(SkyBlockId.CODEC, SkyblockAPICodecs.InternalAttributeDataCodec.codec()),
        "attribute_data.json",
    )

    val data get() = ATTRIBUTE_DATA.get()

    fun save() {
        ATTRIBUTE_DATA.save()
    }

    @GenerateCodec
    internal data class InternalAttributeData(
        override var owned: Int = 0,
        override var syphoned: Int = 0,
        var rarity: SkyBlockRarity?,
    ) : AttributeData {
        override val level: Int get() = attributeLevelData[rarity]?.indexOfLast { it <= syphoned } ?: -1
        override val unlocked: Boolean get() = syphoned >= 1
    }

}
