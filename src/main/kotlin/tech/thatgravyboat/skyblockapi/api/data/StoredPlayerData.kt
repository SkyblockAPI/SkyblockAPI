package tech.thatgravyboat.skyblockapi.api.data

import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.codecs.CodecUtils
import tech.thatgravyboat.skyblockapi.utils.extentions.getEmptyConstructor
import java.util.UUID
import kotlin.reflect.KClass

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
            SkyblockAPICodecs.getCodec<UUID>(),
            codec(it)
        )
    }

    fun get(): T = storedData.get().getOrPut(McPlayer.uuid, data)

    fun save() = storedData.save()


    companion object {
        /** Only use if [T] has an empty constructor. */
        inline operator fun <reified T : Any> invoke(
            file: String,
            version: Int = 0,
            codec: Codec<T> = SkyblockAPICodecs.getCodec<T>(),
        ): StoredPlayerData<T> {
            return create(T::class, file, version) { codec }
        }

        /** Only use if [T] has an empty constructor. */
        inline operator fun <reified T : Any> invoke(
            file: String,
            version: Int = 0,
            noinline codec: (Int) -> Codec<T>,
        ) = create(T::class, file, version, codec)


        fun <T : Any> create(
            kClass: KClass<T>,
            file: String,
            version: Int,
            codec: (Int) -> Codec<T>,
        ): StoredPlayerData<T> {
            val constructor = kClass.getEmptyConstructor()
            requireNotNull(constructor) { "No empty constructor found for ${kClass.simpleName}" }
            val data: () -> T = {
                constructor.callBy(emptyMap())
            }
            return StoredPlayerData(version, data, file, codec)
        }
    }
}
