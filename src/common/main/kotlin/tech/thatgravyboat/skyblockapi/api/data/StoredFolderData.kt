package tech.thatgravyboat.skyblockapi.api.data


import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import java.nio.file.Path
import kotlin.io.path.*

internal class FolderStorage<T : Any>(
    val folder: String,
    val default: T,
    val codec: Codec<T>,
) {
    private val storages = mutableMapOf<String, StoredData<T>>()
    private val defaultPath: Path = McClient.config.resolve("skyblockapi/$folder")

    init {
        load()
    }

    fun load() {
        this.storages.putAll(
            files().mapNotNull {
                val id = it.nameWithoutExtension
                try {
                    id to StoredData(
                        version = 0,
                        data = default,
                        file = "$folder/$id.json",
                        codec = { codec },
                    )
                } catch (e: Exception) {
                    SkyBlockAPI.error("Failed to load storage file: ${it.relativeTo(McClient.config)}", e)
                    null
                }
            },
        )
    }

    fun add(value: T) = set(value.hashCode().toString(), value)

    fun set(id: String, value: T) {
        storages.getOrPut(id) {
            StoredData(
                version = 0,
                dataProvider = { value },
                file = "$folder/$id.json",
                codec = { codec },
            )
        }.apply {
            set(value)
        }
    }

    fun get(id: String): T? = storages[id]?.get()

    fun remove(id: String) {
        storages.remove(id)?.delete()
    }

    private fun files() =
        defaultPath.apply { createDirectories() }.listDirectoryEntries("*.json").toList().filter { it.isRegularFile() && it.extension == "json" }

    internal fun getStorages() = storages
    fun getAll(): Map<String, T> = storages.mapValues { it.value.get() }

    fun refresh() {
        storages.clear()
        load()
    }
}

