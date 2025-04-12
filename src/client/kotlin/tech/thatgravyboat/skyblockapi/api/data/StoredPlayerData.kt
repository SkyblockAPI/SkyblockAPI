package tech.thatgravyboat.skyblockapi.api.data

import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.codecs.CodecUtils
import java.util.*

internal class StoredPlayerData<T : Any>(
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
            codec(it)
        )
    }

    fun get(): T = storedData.get().getOrPut(McPlayer.uuid, data)

    fun save() = storedData.save()
}
