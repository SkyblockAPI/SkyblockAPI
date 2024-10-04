package tech.thatgravyboat.skyblockapi.api.data

import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import net.fabricmc.loader.api.FabricLoader
import tech.thatgravyboat.skyblockapi.extensions.toKtResult
import tech.thatgravyboat.skyblockapi.utils.Logger
import tech.thatgravyboat.skyblockapi.utils.Scheduling
import tech.thatgravyboat.skyblockapi.utils.json.Json.readJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import java.nio.file.Files
import java.nio.file.Path
import kotlin.time.Duration.Companion.milliseconds

private const val SAVE_DELAY = 1000 * 10

class StoredData<T : Any>(
    private var data: T,
    private val codec: Codec<T>,
    private val file: Path,
) {

    private var saveTime = -1L

    init {
        if (Files.isRegularFile(this.file)) {
            try {
                val json = Files.readString(this.file)
                val decoded = json.readJson<JsonElement>()
                this.data = this.codec.parse(JsonOps.INSTANCE, decoded).toKtResult().getOrThrow()
            } catch (e: Exception) {
                Logger.error("Failed to load {} from file", data, e)
            }
        }
    }

    private fun scheduleSave() {
        val diff = (saveTime - System.currentTimeMillis()).coerceAtLeast(0) + 250
        Scheduling.schedule(diff.milliseconds) {
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
            if (!Files.isRegularFile(file)) {
                Files.createDirectories(file.parent)
                Files.createFile(file)
            }
            val json = data.toJson(codec) ?: return Logger.warn("Failed to encode {} to json", data)
            Files.write(file, json.toPrettyString().toByteArray(Charsets.UTF_8))
        } catch (e: Exception) {
            Logger.error("Failed to save {} to file", data, e)
        }
    }

    fun get(): T = data

    fun save() {
        saveTime = System.currentTimeMillis() + SAVE_DELAY
        scheduleSave()
    }

    companion object {
        val defaultPath = FabricLoader.getInstance().configDir.resolve("skyblockapi")
    }
}
