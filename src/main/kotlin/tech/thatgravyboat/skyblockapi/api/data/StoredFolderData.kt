package tech.thatgravyboat.skyblockapi.api.data


import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import java.nio.file.Path
import kotlin.io.path.*

internal class FolderStorage<T : Any>(
    private val version: Int = 0,
    private val folder: String,
    val default: T,
    private val codec: (Int) -> Codec<T>,
) {
    constructor(folder: String, default: T, codec: Codec<T>) : this(0, folder, default, { codec })
    private val storages = mutableMapOf<String, StoredData<T>>()
    private val defaultPath: Path = StoredData.defaultPath.resolve("$folder")

    init {
        load()
    }

    fun load() {
        this.storages.putAll(
            files().mapNotNull {
                val id = it.nameWithoutExtension
                try {
                    id to StoredData(
                        version = version,
                        data = default,
                        file = "$folder/$id.json",
                        differentAlphaData = false,
                        codec = codec,
                    )
                } catch (e: Exception) {
                    SkyBlockAPI.error("Failed to load storage file: ${it.relativeTo(StoredData.defaultPath)}", e)
                    null
                }
            },
        )
    }

    fun add(value: T) = set(value.hashCode().toString(), value)

    fun set(id: String, value: T) {
        storages.getOrPut(id) {
            StoredData(
                version = version,
                data = value,
                file = "$folder/$id.json",
                differentAlphaData = false,
                codec = codec,
            )
        }.set(value)
    }

    fun get(id: String): T? = storages[id]?.get()

    fun remove(id: String) {
        val storage = storages.remove(id) ?: return
        storage.delete()
        StoredData.allStoredDatas.remove(storage)
    }

    private fun files() =
        defaultPath.apply { createDirectories() }.listDirectoryEntries("*.json").toList().filter { it.isRegularFile() && it.extension == "json" }

    internal fun getStorages() = storages
    fun getAll(): Map<String, T> = storages.mapValues { it.value.get() }

    fun refresh() {
        StoredData.allStoredDatas.removeAll(storages.values)
        storages.clear()
        load()
    }
}

