package tech.thatgravyboat.skyblockapi.api.data

import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.codecs.CodecUtils
import java.nio.file.Path
import java.util.*

class StoredPlayerData<T : Any>(
    private val data: () -> T,
    codec: Codec<T>,
    file: Path,
) {
    constructor(data: () -> T, codec: Codec<T>, file: String) : this(data, codec, StoredData.defaultPath.resolve(file))

    private val storedData = StoredData(
        mutableMapOf(),
        CodecUtils.map(
            KCodec.getCodec<UUID>(),
            codec
        ),
        file
    )

    fun get(): T = storedData.get().getOrPut(McPlayer.uuid, data)

    fun save() = storedData.save()
}
