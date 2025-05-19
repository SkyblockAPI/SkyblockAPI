package tech.thatgravyboat.skyblockapi.api.data

import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileAPI
import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.codecs.CodecUtils
import java.util.*

internal class StoredProfileData<T : Any>(
    version: Int = 0,
    private val data: () -> T,
    file: String,
    codec: (Int) -> Codec<T>,
) {

    constructor(data: () -> T, codec: Codec<T>, file: String) : this(
        0,
        data,
        file,
        { codec },
    )

    private val storedData = StoredData(
        version,
        mutableMapOf(),
        file,
    ) {
        CodecUtils.map(
            KCodec.getCodec<UUID>(),
            CodecUtils.map(
                KCodec.getCodec<String>(),
                codec(it),
            ),
        )
    }

    fun get(): T? {
        val storedData = storedData.get()
        val profile = ProfileAPI.profileName ?: return null
        return storedData.getOrPut(McPlayer.uuid, ::mutableMapOf).getOrPut(profile, data)
    }

    fun save() = storedData.save()
}
