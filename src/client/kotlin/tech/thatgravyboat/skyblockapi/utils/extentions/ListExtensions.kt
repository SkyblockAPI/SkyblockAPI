package tech.thatgravyboat.skyblockapi.utils.extentions

/**
 * Splits a list into chunks based on a predicate.
 * The predicate will be true when a new chunk should be created, will include the element in the new chunk.
 */
internal fun <T> List<T>.chunked(predicate: (T) -> Boolean): MutableList<MutableList<T>> {
    val chunks = mutableListOf<MutableList<T>>()
    for (element in this) {
        val currentChunk = chunks.lastOrNull()
        if (currentChunk == null || predicate(element)) {
            chunks.add(mutableListOf(element))
        } else {
            currentChunk.add(element)
        }
    }
    return chunks
}

internal inline fun <T> List<T>.peek(crossinline block: (T) -> Unit): List<T> {
    for (element in this) {
        block(element)
    }
    return this
}

