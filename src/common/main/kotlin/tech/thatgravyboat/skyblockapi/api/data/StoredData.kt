package tech.thatgravyboat.skyblockapi.api.data

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import net.fabricmc.loader.api.FabricLoader
import org.apache.commons.io.FileUtils
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.utils.Logger
import tech.thatgravyboat.skyblockapi.utils.Scheduling
import tech.thatgravyboat.skyblockapi.utils.extentions.getEmptyConstructor
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.json.JsonObject
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.ScheduledFuture
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.milliseconds

private const val SAVE_DELAY = 1000 * 10

internal class StoredData<T : Any>(
    private val version: Int = 0,
    private var data: T,
    file: String,
    private val codec: (Int) -> Codec<T>,
) {

    constructor(data: T, codec: Codec<T>, file: String) : this(0, data, file, { codec })

    private val file: Path = defaultPath.resolve(file)
    private var lastScheduler: ScheduledFuture<*>? = null
    private var saveTime: Long = -1L
    private var loadedData: JsonElement? = null

    init {
        if (Files.isRegularFile(this.file)) {
            try {
                val json = Files.readString(this.file)
                this.loadedData = json.readJson<JsonElement>()
            } catch (e: Exception) {
                Logger.error("Failed to load {} from file", this.loadedData ?: "")
                e.printStackTrace()
            }
        }
    }

    private fun load() {
        if (this.loadedData != null) {
            try {
                val data = this.loadedData as? JsonObject
                if (data != null && data.has("@skyblockapi:version") && data.has("@skyblockapi:data")) {
                    val version = data.get("@skyblockapi:version").asInt
                    val dataElement = data.getAsJsonObject("@skyblockapi:data")

                    this.data = dataElement.toDataOrThrow(this.codec(version))
                } else {
                    this.data = this.loadedData.toDataOrThrow(this.codec(0))
                }
            } catch (e: Exception) {
                Logger.error("Failed to load {} data", this.loadedData ?: "")
                e.printStackTrace()
            }
            this.loadedData = null
        }
    }

    private fun scheduleSave() {
        this.loadedData = null
        this.lastScheduler?.cancel(false)
        val diff = (this.saveTime - System.currentTimeMillis()).coerceAtLeast(0) + 250
        this.lastScheduler = Scheduling.schedule(diff.milliseconds) {
            if (System.currentTimeMillis() >= saveTime && saveTime != -1L) {
                saveToSystem()
                this.saveTime = -1L
            } else {
                scheduleSave()
            }
        }
    }

    private fun saveToSystem() {
        try {
            val version = this.version
            val codec = this.codec(version)
            val json = JsonObject {
                this["@skyblockapi:version"] = version
                this["@skyblockapi:data"] = data.toJson(codec) ?: return Logger.warn("Failed to encode {} to json", data)
            }
            FileUtils.write(file.toFile(), json.toPrettyString(), Charsets.UTF_8)
            Logger.debug("saved {}", file)
        } catch (e: Exception) {
            Logger.error("Failed to save {} to file", data)
            e.printStackTrace()
        }
    }

    fun get(): T {
        load()
        return this.data
    }

    fun save() {
        saveTime = System.currentTimeMillis() + SAVE_DELAY
        scheduleSave()
    }

    companion object {
        val defaultPath: Path = FabricLoader.getInstance().configDir.resolve("skyblockapi")

        /** Only use if [T] has an empty constructor. */
        inline operator fun <reified T : Any> invoke(
            file: String,
            version: Int = 0,
            codec: Codec<T> = SkyblockAPICodecs.getCodec<T>(),
        ): StoredData<T> {
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
        ): StoredData<T> {
            val constructor = kClass.getEmptyConstructor()
            requireNotNull(constructor) { "No empty constructor found for ${kClass.simpleName}" }
            val data = constructor.callBy(emptyMap())
            return StoredData(version, data, file, codec)
        }
    }
}
