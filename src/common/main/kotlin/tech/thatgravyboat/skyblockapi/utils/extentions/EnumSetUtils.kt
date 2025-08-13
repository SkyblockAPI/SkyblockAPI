package tech.thatgravyboat.skyblockapi.utils.extentions

import java.util.*

// todo: move into enum extensions with 1.21.6

inline fun <reified E : Enum<E>> emptyEnumSet(): EnumSet<E> = EnumSet.noneOf(E::class.java)

inline fun <reified E : Enum<E>> enumSetOf(): EnumSet<E> = emptyEnumSet<E>()

inline fun <reified E : Enum<E>> enumSetOf(element: E) = emptyEnumSet<E>().apply { add(element) }

inline fun <reified E : Enum<E>> enumSetOf(vararg elements: E): EnumSet<E> =
    if (elements.isEmpty()) emptyEnumSet<E>()
    else elements.toCollection(enumSetOf<E>())


inline fun <reified E : Enum<E>> fullEnumSetOf(): EnumSet<E> = EnumSet.allOf(E::class.java)

inline fun <reified E : Enum<E>> Set<E>.toEnumSet(): EnumSet<E> =
    if (isEmpty()) emptyEnumSet<E>() else EnumSet.copyOf(this)

operator fun <E : Enum<E>> E.rangeTo(other: E): EnumSet<E> {
    return EnumSet.range(this, other)
}
