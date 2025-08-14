package tech.thatgravyboat.skyblockapi.api.data.stored

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
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
}
