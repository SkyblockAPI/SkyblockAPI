package tech.thatgravyboat.skyblockapi.api.data

import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileAPI
import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.codecs.CodecUtils
import tech.thatgravyboat.skyblockapi.utils.extentions.getEmptyConstructor
import java.util.*
import kotlin.reflect.KClass

internal class StoredProfileData<T : Any>(
    version: Int = 0,
    private val data: () -> T,
    file: String,
    codec: (Int) -> Codec<T>,
) {

    companion object {
        inline operator fun <reified T : Any> invoke(file: String): StoredProfileData<T> {
            val codec = KCodec.getCodec<T>()
            return create(T::class, 0, file) { codec }
        }

        inline operator fun <reified T : Any> invoke(codec: Codec<T>, file: String) =
            create(T::class, 0, file) { codec }

        inline operator fun <reified T : Any> invoke(
            version: Int = 0,
            file: String,
            noinline codec: (Int) -> Codec<T>,
        ) = create(T::class, version, file, codec)


        fun <T : Any> create(
            kClass: KClass<T>,
            version: Int = 0,
            file: String,
            codec: (Int) -> Codec<T>,
        ): StoredProfileData<T> {
            val constructor = kClass.getEmptyConstructor()
            requireNotNull(constructor) { "No empty constructor found for ${kClass.simpleName}" }
            val data: () -> T = {
                constructor.callBy(emptyMap())
            }
            return StoredProfileData(version, data, file, codec)
        }
    }

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
