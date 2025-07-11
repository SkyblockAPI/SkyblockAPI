package tech.thatgravyboat.skyblockapi.api.data

import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileChangeEvent
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileAPI
import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.codecs.CodecUtils
import tech.thatgravyboat.skyblockapi.utils.extentions.getEmptyConstructor
import java.util.*
import kotlin.reflect.KClass

internal class StoredProfileData<T : Any>(
    version: Int = 0,
    private val data: () -> T,
    file: String,
    autoLoadOnProfileSwap: Boolean = false,
    codec: (Int) -> Codec<T>,
) {
    constructor(data: () -> T, codec: Codec<T>, file: String, autoLoadOnProfileSwap: Boolean = false) : this(
        0,
        data,
        file,
        autoLoadOnProfileSwap,
        { codec },
    )

    companion object {
        /** Only use if [T] has an empty constructor. */
        inline operator fun <reified T : Any> invoke(
            file: String,
            version: Int = 0,
            autoLoadOnProfileSwap: Boolean = false,
            codec: Codec<T> = SkyblockAPICodecs.getCodec<T>(),
        ): StoredProfileData<T> {
            return create(T::class, file, version, autoLoadOnProfileSwap) { codec }
        }

        /** Only use if [T] has an empty constructor. */
        inline operator fun <reified T : Any> invoke(
            file: String,
            version: Int = 0,
            autoLoadOnProfileSwap: Boolean = false,
            noinline codec: (Int) -> Codec<T>,
        ) = create(T::class, file, version, autoLoadOnProfileSwap, codec)


        private fun <T : Any> create(
            kClass: KClass<T>,
            file: String,
            version: Int,
            autoLoadOnProfileSwap: Boolean = false,
            codec: (Int) -> Codec<T>,
        ): StoredProfileData<T> {
            val constructor = kClass.getEmptyConstructor()
            requireNotNull(constructor) { "No empty constructor found for ${kClass.simpleName}" }
            val data: () -> T = {
                constructor.callBy(emptyMap())
            }
            return StoredProfileData(version, data, file, autoLoadOnProfileSwap, codec)
        }
    }

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

    init {
        if (autoLoadOnProfileSwap) {
            SkyBlockAPI.eventBus.register<ProfileChangeEvent> { storedData.loadAsync() }
        }
    }

    fun get(): T? {
        val storedData = storedData.get()
        val profile = ProfileAPI.profileName ?: return null
        return storedData.getOrPut(McPlayer.uuid, ::mutableMapOf).getOrPut(profile, data)
    }

    fun save() = storedData.save()
}
