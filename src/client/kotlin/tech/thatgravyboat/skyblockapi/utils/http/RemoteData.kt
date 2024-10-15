package tech.thatgravyboat.skyblockapi.utils.http

import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import tech.thatgravyboat.skyblockapi.api.data.StoredData
import tech.thatgravyboat.skyblockapi.utils.Logger
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import tech.thatgravyboat.skyblockapi.utils.runCatchBlocking
import java.nio.file.Path
import java.util.*

class RemoteData<T : Any>(
    codec: Codec<T>,
    url: String,
    file: Path,
) {

    constructor(codec: Codec<T>, url: String, file: String) :
        this(codec, url, StoredData.defaultPath.resolve("remote").resolve(file))

    private class Data<T : Any>(var etag: String, var data: T?)
    private val data: StoredData<Data<T>> = StoredData(
        Data("", null),
        RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.STRING.fieldOf("etag").forGetter { it.etag },
                codec.optionalFieldOf("data").forGetter { Optional.ofNullable(it.data) },
            ).apply(instance) { tag, data -> Data(tag, data.orElse(null)) }
        },
        file,
    )

    init {
        runCatchBlocking {
            Http.head(url) {
                val etag = headers["ETag"]?.firstOrNull() ?: return@head
                val localData = data.get()
                val oldTag = localData.etag
                if (etag != oldTag) {
                    Http.getResult<JsonElement>(url, errorFactory = ::RuntimeException)
                        .fold(
                            { json ->
                                localData.data = json.toDataOrThrow(codec)
                                localData.etag = etag
                                data.save()
                            },
                            { throw it },
                        )

                }
            }
        }.onFailure {
            Logger.error("Failed to load remote data from {}", url)
            it.printStackTrace()
        }
    }

    fun get(): T? = data.get().data

    operator fun getValue(thisRef: Any?, property: Any?): T? = get()

}
