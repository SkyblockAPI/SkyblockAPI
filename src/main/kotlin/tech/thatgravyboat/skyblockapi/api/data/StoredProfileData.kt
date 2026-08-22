package tech.thatgravyboat.skyblockapi.api.data

import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileChangeEvent
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileAPI
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.codecs.CodecUtils
import tech.thatgravyboat.skyblockapi.utils.extentions.getEmptyConstructor
import java.util.UUID
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

        private val _allProfileData = mutableListOf<StoredProfileData<*>>()
        internal val allProfileData: List<StoredProfileData<*>> get() = _allProfileData
    }

    private val storedData = StoredData(
        version,
        mutableMapOf(),
        file,
    ) {
        CodecUtils.map(
            SkyblockAPICodecs.getCodec<UUID>(),
            CodecUtils.map(
                SkyblockAPICodecs.getCodec<String>(),
                codec(it),
            ),
        )
    }

    init {
        if (autoLoadOnProfileSwap) {
            SkyBlockAPI.eventBus.register<ProfileChangeEvent> { storedData.loadAsync() }
        }
        _allProfileData.add(this)
    }

    fun get(): T? {
        val profile = ProfileAPI.profileName ?: return null
        val storedData = storedData.get()
        return storedData.getOrPut(McPlayer.uuid, ::mutableMapOf).getOrPut(profile, data)
    }

    /**
     * Helper function for editing data and saving.
     * If you don't want the data to be saved, you have to edit return `null` to the lambda or just return in the current function altogether.
     * Returning in the current function alltogether is generally better, since when compiled it makes it avoid dealing with the nullable Unit,
     * and simply calls [save] after editing
     * Since Kotlin returns [Unit] in lambdas when not specified, those are usually not needed to be actually written.
     *
     * Examples:
     * ```kt
     * STORED_DATA.edit {
     *     if (this.value == newValue) return
     *     this.value = newValue
     * }
     * ```
     * ```kt
     * STORED_DATA.edit {
     *     if (this.value == newValue) return@edit null
     *     this.value = newValue
     * }
     * ```
     * */
    inline fun edit(edit: T.() -> Unit?) {
        val data = get() ?: return
        if (edit(data) != null) save()
    }

    fun removeProfile(name: String) {
        if (storedData.get()[McPlayer.uuid]?.remove(name) != null) save()
    }

    fun save() = storedData.save()
}
