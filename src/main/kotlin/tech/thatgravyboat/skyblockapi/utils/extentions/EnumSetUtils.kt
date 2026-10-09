package tech.thatgravyboat.skyblockapi.utils.extentions

import java.util.*

// todo: move into enum extensions with 1.21.6

public inline fun <reified E : Enum<E>> emptyEnumSet(): EnumSet<E> = EnumSet.noneOf(E::class.java)

public inline fun <reified E : Enum<E>> enumSetOf(): EnumSet<E> = emptyEnumSet<E>()

public inline fun <reified E : Enum<E>> enumSetOf(element: E): EnumSet<E> = emptyEnumSet<E>().apply { add(element) }

public inline fun <reified E : Enum<E>> enumSetOf(vararg elements: E): EnumSet<E> = elements.toEnumSet<E>()

public inline fun <reified E : Enum<E>> Array<out E>.toEnumSet(): EnumSet<E> = toCollection(enumSetOf<E>())
public inline fun <reified E : Enum<E>> Collection<E>.toEnumSet(): EnumSet<E> {
    return if (isEmpty()) emptyEnumSet<E>() else EnumSet.copyOf(this)
}

public inline fun <reified E : Enum<E>> fullEnumSetOf(): EnumSet<E> = EnumSet.allOf(E::class.java)

public operator fun <E : Enum<E>> E.rangeTo(other: E): EnumSet<E> = EnumSet.range(this, other)
