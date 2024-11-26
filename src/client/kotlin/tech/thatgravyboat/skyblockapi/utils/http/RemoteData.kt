package tech.thatgravyboat.skyblockapi.utils.http

import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import tech.thatgravyboat.skyblockapi.api.data.StoredData
import tech.thatgravyboat.skyblockapi.utils.Logger
import tech.thatgravyboat.skyblockapi.utils.extentions.forNullGetter
import tech.thatgravyboat.skyblockapi.utils.json.Json.toDataOrThrow
import tech.thatgravyboat.skyblockapi.utils.runCatchBlocking
import java.nio.file.Path
import java.util.*

class RemoteData<T : Any> internal constructor(
    codec: Codec<T>,
    url: String,
    file: Path,
) {

    constructor(codec: Codec<T>, url: String, file: String) :
        this(codec, url, StoredData.defaultPath.resolve("remote").resolve(file))

    private class Data<T : Any>(val local: Boolean, var etag: String, var data: T?)
    private val data: StoredData<Data<T>> = StoredData(
        Data(false, "", null),
        RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.BOOL.optionalFieldOf("local", false).forGetter(Data<T>::local),
                Codec.STRING.fieldOf("etag").forGetter(Data<T>::etag),
                codec.optionalFieldOf("data").forNullGetter(Data<T>::data),
            ).apply(instance) { local, tag, data -> Data(local, tag, data.orElse(null)) }
        },
        file,
    )

    init {
        val localData = data.get()
        if (!localData.local) {
            runCatchBlocking {
                Http.head(url) {
                    val etag = headers["ETag"]?.firstOrNull() ?: error("No ETag present")
                    val oldTag = localData.etag

                    if (etag != oldTag) {
                        Http.getResult<JsonElement>(url)
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
    }

    fun get(): T? = data.get().data

    operator fun getValue(thisRef: Any?, property: Any?): T? = get()

}
