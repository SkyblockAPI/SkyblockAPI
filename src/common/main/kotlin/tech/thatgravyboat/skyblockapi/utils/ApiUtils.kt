package tech.thatgravyboat.skyblockapi.utils

import kotlinx.coroutines.runBlocking

internal fun runCatchBlocking(block: suspend () -> Unit) = runBlocking {
    runCatching { block() }
}
