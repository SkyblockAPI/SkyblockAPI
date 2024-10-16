package tech.thatgravyboat.skyblockapi.api.data

import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileAPI
import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.codecs.CodecUtils
import java.nio.file.Path
import java.util.*

class StoredProfileData<T : Any>(
    private val data: () -> T,
    codec: Codec<T>,
    file: Path,
) {
    constructor(data: () -> T, codec: Codec<T>, file: String) : this(data, codec, StoredData.defaultPath.resolve(file))

    private val storedData = StoredData(
        mutableMapOf(),
        CodecUtils.map(
            KCodec.getCodec<UUID>(),
            CodecUtils.map(
                KCodec.getCodec<String>(),
                codec
            )
        ),
        file
    )

    fun get(): T? {
        val profile = ProfileAPI.profileName ?: return null
        return storedData.get()
            .getOrPut(McPlayer.uuid, ::mutableMapOf)
            .getOrPut(profile, data)
    }

    fun save() = storedData.save()
}
