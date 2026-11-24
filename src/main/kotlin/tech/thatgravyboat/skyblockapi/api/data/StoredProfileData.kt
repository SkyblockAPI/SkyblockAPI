package tech.thatgravyboat.skyblockapi.api.data

import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileAPI
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.codecs.CodecUtils
import tech.thatgravyboat.skyblockapi.utils.extentions.getEmptyConstructor
import java.util.UUID
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KClass

internal class StoredProfileData<T : Any>(
    version: Int = 0,
    private val data: () -> T,
    file: String,
    differentAlphaData: Boolean = true,
    codec: (Int) -> Codec<T>,
) {
    constructor(data: () -> T, codec: Codec<T>, file: String, differentAlphaData: Boolean = true) : this(
        0,
        data,
        file,
        differentAlphaData,
        { codec },
    )

    companion object {
        /** Only use if [T] has an empty constructor. */
        inline operator fun <reified T : Any> invoke(
            file: String,
            version: Int = 0,
            differentAlphaData: Boolean = true,
            codec: Codec<T> = SkyblockAPICodecs.getCodec<T>(),
        ): StoredProfileData<T> {
            return create(T::class, file, version, differentAlphaData) { codec }
        }

        /** Only use if [T] has an empty constructor. */
        inline operator fun <reified T : Any> invoke(
            file: String,
            version: Int = 0,
            differentAlphaData: Boolean = true,
            noinline codec: (Int) -> Codec<T>,
        ) = create(T::class, file, version, differentAlphaData, codec)


        private fun <T : Any> create(
            kClass: KClass<T>,
            file: String,
            version: Int,
            differentAlphaData: Boolean = true,
            codec: (Int) -> Codec<T>,
        ): StoredProfileData<T> {
            val constructor = kClass.getEmptyConstructor()
            requireNotNull(constructor) { "No empty constructor found for ${kClass.simpleName}" }
            val data: () -> T = {
                constructor.callBy(emptyMap())
            }
            return StoredProfileData(version, data, file, differentAlphaData, codec)
        }

        private val _allProfileData = mutableListOf<StoredProfileData<*>>()
        internal val allProfileData: List<StoredProfileData<*>> get() = _allProfileData
    }

    private val storedData = StoredData(
        version,
        mutableMapOf(),
        file,
        differentAlphaData,
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
        _allProfileData.add(this)
    }

    fun get(): T? {
        val profile = ProfileAPI.profileName ?: return null
        val storedData = storedData.get()
        return storedData.getOrPut(McPlayer.uuid, ::mutableMapOf).getOrPut(profile, data)
    }

    fun deleteCurrent() {
        val profile = ProfileAPI.profileName ?: return
        removeProfile(profile)
    }

    /** Only saves if [editIf] returns true. Also returns the result of [editIf] */
    inline fun editIf(edit: T.() -> Boolean): Boolean {
        contract {
            callsInPlace(edit, InvocationKind.AT_MOST_ONCE)
        }
        val edited = edit(get() ?: return false)
        if (edited) save()
        return edited
    }

    /**
     * Avoids saving if you return inside the function
     * @sample tech.thatgravyboat.skyblockapi.api.data.stored.ProfileStorage.setBingoRank
     */
    inline fun edit(edit: T.() -> Unit) {
        contract {
            callsInPlace(edit, InvocationKind.AT_MOST_ONCE)
        }
        edit(get() ?: return)
        save()
    }

    fun removeProfile(name: String) {
        // Deletes both normal data and alpha data
        val maps = listOfNotNull(storedData.getNormalData(), storedData.getAlphaData())
        val removedCount = maps.count { it[McPlayer.uuid]?.remove(name) != null }
        if (removedCount > 0) save()
    }

    fun save() = storedData.save()
}
