package tech.thatgravyboat.skyblockapi.utils.extentions

import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import java.util.*

// todo: move into enum extensions with 1.21.6

inline fun <reified E : Enum<E>> emptyEnumSet(): EnumSet<E> = EnumSet.noneOf(E::class.java)

inline fun <reified E : Enum<E>> enumSetOf(): EnumSet<E> = emptyEnumSet<E>()

inline fun <reified E : Enum<E>> enumSetOf(element: E) = emptyEnumSet<E>().apply { add(element) }

inline fun <reified E : Enum<E>> enumSetOf(vararg elements: E): EnumSet<E> = elements.toEnumSet<E>()

inline fun <reified E : Enum<E>> Array<out E>.toEnumSet() = toCollection(enumSetOf<E>())
inline fun <reified E : Enum<E>> Collection<E>.toEnumSet(): EnumSet<E> {
    return if (isEmpty()) emptyEnumSet<E>() else EnumSet.copyOf(this)
}

inline fun <reified E : Enum<E>> fullEnumSetOf(): EnumSet<E> = EnumSet.allOf(E::class.java)

@RemoveNextVersion // Remove in favor of Collection.toEnumSet()
inline fun <reified E : Enum<E>> Set<E>.toEnumSet(): EnumSet<E> =
    if (isEmpty()) emptyEnumSet<E>() else EnumSet.copyOf(this)

operator fun <E : Enum<E>> E.rangeTo(other: E): EnumSet<E> = EnumSet.range(this, other)
