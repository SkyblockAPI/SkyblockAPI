package tech.thatgravyboat.skyblockapi.api.data

import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.utils.Logger
import tech.thatgravyboat.skyblockapi.utils.Scheduling
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import java.nio.file.Files
import java.nio.file.Path
import kotlin.time.Duration.Companion.milliseconds

private const val SAVE_DELAY = 1000 * 10

class StoredData<T : Any>(
    private val data: T,
    private val codec: Codec<T>,
    private val file: Path,
) {

    private var saveTime = -1L

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
        if (!Files.isRegularFile(file)) Files.createFile(file)
        val json = data.toJson(codec) ?: return Logger.warn("Failed to encode {} to json", data)
        Files.write(file, json.toPrettyString().toByteArray(Charsets.UTF_8))
    }

    fun get(): T = data

    fun save() {
        saveTime = System.currentTimeMillis() + SAVE_DELAY
        scheduleSave()
    }
}
