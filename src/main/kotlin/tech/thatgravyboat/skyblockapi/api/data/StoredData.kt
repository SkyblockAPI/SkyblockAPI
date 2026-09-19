package tech.thatgravyboat.skyblockapi.api.data

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mojang.serialization.Codec
import me.owdding.ktmodules.Module
import org.apache.commons.io.FileUtils
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.hypixel.NewHypixelAlphaDetectedEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.Logger
import tech.thatgravyboat.skyblockapi.utils.extentions.getEmptyConstructor
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJsonOrThrow
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.json.JsonObject
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import kotlin.io.path.createParentDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.relativeTo
import kotlin.reflect.KClass

internal class StoredData<T : Any>(
    private val version: Int = 0,
    private val factory: () -> T,
    file: String,
    private val differentAlphaData: Boolean = true,
    private val codec: (Int) -> Codec<T>,
) {
    constructor(version: Int = 0, data: T, file: String, differentAlphaData: Boolean = true, codec: (Int) -> Codec<T>)
        : this(version, { data }, file, differentAlphaData, codec)
    constructor(data: T, codec: Codec<T>, file: String, differentAlphaData: Boolean = true)
        : this(0, { data }, file, differentAlphaData, { codec })


    fun get(): T = if (shouldUseAlphaData()) getOrCreateAlphaData() else data

    fun set(value: T) {
        if (shouldUseAlphaData()) this.alphaData = value
        else this.data = value
        save()
    }

    fun save() {
        if (shouldUseAlphaData()) requiresAlphaSave.add(this)
        else requiresSave.add(this)
    }

    fun delete() {
        deletePath(path)
        this.data = factory()
        deletePath(alphaPath)
        alphaData = null
    }

    internal fun getNormalData(): T = data
    internal fun getAlphaData(): T? = alphaData

    init {
        allStoredDatas.add(this)
    }

    private val fileName = "${file.removeSuffix(".json")}.json"
    private val path: Path = defaultPath.resolve(this.fileName)
    private val alphaPath: Path = defaultAlphaPath.resolve(this.fileName)

    private var data: T
    private var alphaData: T? = null

    private fun shouldUseAlphaData() = differentAlphaData && LocationAPI.onAlpha

    private fun copyData(): T {
        try {
            Logger.debug("Creating copy of data for alpha data")
            // we convert to json and then back to make a new copy of the data and not just a reference to it
            return data.toJsonOrThrow(currentCodec).toDataOrThrow(currentCodec)
        } catch (e: Exception) {
            Logger.error("Failed to copy {} to alphaData ", data)
            e.printStackTrace()
            return factory()
        }
    }

    private fun getOrCreateAlphaData(): T {
        var alphaData = alphaData
        if (alphaData == null) {
            Logger.debug("Loading alpha data")
            // we use the current normal data as a default for alpha data
            alphaData = loadData(alphaPath, ::copyData)
            this.alphaData = alphaData
        }
        return alphaData
    }

    private fun loadData(path: Path, default: () -> T): T {
        if (!path.exists()) {
            path.createParentDirectories()
            return default()
        } else {
            try {
                val json = JsonParser.parseString(path.readText()) as? JsonObject
                if (json != null && json.has("@skyblockapi:version") && json.has("@skyblockapi:version")) {
                    val version = json.get("@skyblockapi:version").asInt
                    val dataElement = json.getAsJsonObject("@skyblockapi:data")

                    return dataElement.toDataOrThrow(this.codec(version))
                } else {
                    return json.toDataOrThrow(this.codec(0))
                }
            } catch (e: Exception) {
                Logger.error("Failed to load data from {}", path.relativeTo(defaultPath))
                e.printStackTrace()
                return default()
            }
        }
    }

    init {
        this.data = loadData(path, factory)
    }

    private val currentCodec = codec(version)

    private fun deletePath(path: Path) {
        try {
            path.deleteIfExists()
            Logger.info("deleted {}", path.relativeTo(defaultPath))
        } catch (e: Throwable) {
            Logger.error("Failed to delete file {}", path.relativeTo(defaultPath))
            e.printStackTrace()
        }
    }

    private fun deleteAlpha() {
        this.alphaData = null
        deletePath(alphaPath)
    }

    private fun saveToSystem() {
        savePath(data, path)
    }

    private fun savePath(data: T, path: Path) {
        try {
            val version = this.version
            val json = JsonObject {
                this["@skyblockapi:version"] = version
                this["@skyblockapi:data"] = data.toJsonOrThrow(currentCodec)
            }
            FileUtils.write(path.toFile(), json.toPrettyString(), Charsets.UTF_8)
            Logger.info("saved {}", path.relativeTo(defaultPath))
        } catch (e: Throwable) {
            Logger.error("Failed to save {} to file", data)
            e.printStackTrace()
        }
    }
    private fun saveAlphaToSystem() {
        val alphaData = alphaData ?: return
        savePath(alphaData, alphaPath)
    }

    @Module
    internal companion object {
        val allStoredDatas = mutableListOf<StoredData<*>>()
        val defaultPath: Path = McClient.config.resolve("skyblockapi")
        val defaultAlphaPath: Path = defaultPath.resolve("alpha")

        private val requiresSave = mutableSetOf<StoredData<*>>()
        private val requiresAlphaSave = mutableSetOf<StoredData<*>>()

        inline fun <reified T : Any> clearAndRun(collection: MutableCollection<T>, crossinline block: (T) -> Unit) {
            val copy = collection.toTypedArray<T>()
            collection.clear()
            if (copy.isEmpty()) return
            CompletableFuture.runAsync {
                copy.forEach(block)
            }
        }

        @Subscription(NewHypixelAlphaDetectedEvent::class)
        fun onNewAlpha() {
            allStoredDatas.forEach(StoredData<*>::deleteAlpha)
        }

        @TimePassed("10s")
        @Subscription(TickEvent::class)
        fun onTick() {
            clearAndRun(requiresSave, StoredData<*>::saveToSystem)
            clearAndRun(requiresAlphaSave, StoredData<*>::saveAlphaToSystem)
        }

        //region Stored Data Creation
        /** Only use if [T] has an empty constructor. */
        inline operator fun <reified T : Any> invoke(
            file: String,
            version: Int = 0,
            differentAlphaData: Boolean = true,
            codec: Codec<T> = SkyblockAPICodecs.getCodec<T>(),
        ): StoredData<T> {
            return create(T::class, file, version, differentAlphaData) { codec }
        }

        /** Only use if [T] has an empty constructor. */
        inline operator fun <reified T : Any> invoke(
            file: String,
            version: Int = 0,
            differentAlphaData: Boolean = true,
            noinline codec: (Int) -> Codec<T>,
        ) = create(T::class, file, version, differentAlphaData, codec)


        fun <T : Any> create(
            kClass: KClass<T>,
            file: String,
            version: Int,
            differentAlphaData: Boolean = true,
            codec: (Int) -> Codec<T>,
        ): StoredData<T> {
            val constructor = kClass.getEmptyConstructor()
            requireNotNull(constructor) { "No empty constructor found for ${kClass.simpleName}" }
            val data = constructor.callBy(emptyMap())
            return StoredData(version, data, file, differentAlphaData, codec)
        }
        //endregion
    }
}
