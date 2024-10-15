package tech.thatgravyboat.skyblockapi.api.data

import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import net.fabricmc.loader.api.FabricLoader
import org.apache.commons.io.FileUtils
import tech.thatgravyboat.skyblockapi.utils.Logger
import tech.thatgravyboat.skyblockapi.utils.Scheduling
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toData
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.ScheduledFuture
import kotlin.time.Duration.Companion.milliseconds

private const val SAVE_DELAY = 1000 * 10

class StoredData<T : Any>(
    private var data: T,
    private val codec: Codec<T>,
    private val file: Path,
) {
    constructor(data: T, codec: Codec<T>, file: String) : this(data, codec, defaultPath.resolve(file))

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
                this.data = this.loadedData.toData(this.codec)!!
                this.loadedData = null
            } catch (e: Exception) {
                Logger.error("Failed to load {} data", this.loadedData ?: "")
                e.printStackTrace()
            }
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
            val json = data.toJson(codec) ?: return Logger.warn("Failed to encode {} to json", data)
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
    }
}
