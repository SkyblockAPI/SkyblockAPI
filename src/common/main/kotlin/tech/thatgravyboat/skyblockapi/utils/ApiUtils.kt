package tech.thatgravyboat.skyblockapi.utils

import kotlinx.coroutines.runBlocking

internal fun runCatchBlocking(block: suspend () -> Unit) = runBlocking {
    runCatching { block() }
}

internal inline fun <R> runCatchingWithPrint(block: () -> R): Result<R> = runCatching(block)
    .onFailure(Throwable::printStackTrace)
