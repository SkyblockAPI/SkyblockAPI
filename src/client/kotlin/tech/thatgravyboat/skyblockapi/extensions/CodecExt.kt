package tech.thatgravyboat.skyblockapi.extensions

import com.google.gson.JsonElement
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.JsonOps

fun <T> DataResult<T>.toKtResult(): Result<T> {
    return if (this.error().isPresent) {
        Result.failure(Exception(this.error().get().message()))
    } else {
        Result.success(this.result().get())
    }
}

fun <T> Codec<T>.toJson(value: T): Result<JsonElement> {
    return this.encodeStart(JsonOps.INSTANCE, value).toKtResult()
}
