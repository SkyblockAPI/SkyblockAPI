package tech.thatgravyboat.skyblockapi.utils.extentions

/**
 * Splits a list into chunks based on a predicate.
 * The predicate will be true when a new chunk should be created, will include the element in the new chunk.
 */
fun <T> List<T>.chunked(predicate: (T) -> Boolean): MutableList<MutableList<T>> {
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

fun <T> Iterable<T>.firstOrElseLast(predicate: (T) -> Boolean): T {
    return this.firstOrNull(predicate) ?: this.last()
}

inline fun <T> List<T>.peek(crossinline block: (T) -> Unit): List<T> {
    for (element in this) {
        block(element)
    }
    return this
}

fun <T> MutableIterable<T>.clearAnd(action: (T) -> Unit) {
    val it = iterator()
    while (it.hasNext()) {
        action(it.next())
        it.remove()
    }
}

fun <T> List<T>.asReversedIterator(): Iterator<T> {
    val list = this
    return object : Iterator<T> {

        val originalSize = list.size
        var index = originalSize - 1

        override fun hasNext(): Boolean = index >= 0

        override fun next(): T {
            if (originalSize != list.size) {
                throw ConcurrentModificationException()
            } else if (!hasNext()) {
                throw NoSuchElementException()
            } else {
                return list[index--]
            }
        }

    }
}

fun <K> MutableMap<K, Int>.addOrPut(key: K, number: Int): Int = merge(key, number, Int::plus)!!

fun <K> MutableMap<K, Double>.addOrPut(key: K, number: Double): Double = merge(key, number, Double::plus)!!

fun <K> MutableMap<K, Float>.addOrPut(key: K, number: Float): Float = merge(key, number, Float::plus)!!

fun <K> MutableMap<K, Long>.addOrPut(key: K, number: Long): Long = merge(key, number, Long::plus)!!

internal fun <T : Any> MutableCollection<T>.addIfNotNull(element: T?): Boolean = element?.let { add(it) } ?: false

fun <K, V> Map<K, V>.mapValuesNotNull(transform: (Map.Entry<K, V>) -> V?): Map<K, V> {
    return this.mapNotNull { transform(it)?.let { value -> it.key to value } }.toMap()
}

@Suppress("UNCHECKED_CAST")
fun <K, V> Map<K, V?>.filterValuesNotNull(): Map<K, V> = this.filterValues { it != null } as Map<K, V>

@Suppress("UNCHECKED_CAST")
fun <K, V> Map<K?, V>.filterKeysNotNull(): Map<K, V> = this.filterKeys { it != null } as Map<K, V>

inline fun <T, K> Iterable<T>.associateByNotNull(keySelector: (T) -> K?): Map<K, T> = buildMap {
    for (element in this@associateByNotNull) put(keySelector(element) ?: continue, element)
}

inline fun <T> List<T>.sublistAfter(predicate: (T) -> Boolean): List<T> {
    val index = this.indexOfFirst(predicate)
    return if (index == -1) emptyList() else this.subList(index + 1, this.size)
}
