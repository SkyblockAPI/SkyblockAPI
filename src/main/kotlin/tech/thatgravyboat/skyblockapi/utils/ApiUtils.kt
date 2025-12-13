package tech.thatgravyboat.skyblockapi.utils

import kotlinx.coroutines.runBlocking
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI

internal fun runCatchBlocking(block: suspend () -> Unit) = runBlocking {
    runCatching { block() }
}

internal inline fun <R> runCatchingWithPrint(block: () -> R): Result<R> = runCatching(block).onFailure {
    SkyBlockAPI.error("An error occurred!", it)
}
