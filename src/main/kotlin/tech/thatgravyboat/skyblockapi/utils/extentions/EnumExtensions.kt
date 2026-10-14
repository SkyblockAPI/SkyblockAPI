package tech.thatgravyboat.skyblockapi.utils.extentions


public inline fun <reified T : Enum<T>> valueOfOrNull(name: String): T? = try { enumValueOf<T>(name) } catch (_: Throwable) { null }


public inline fun <reified E : Enum<E>> E.nextCycling(offset: Int = 1): E = enumValues<E>().let { it[(ordinal + offset) % it.size] }
public inline fun <reified E : Enum<E>> E.next(offset: Int = 1): E? = enumValues<E>().getOrNull(ordinal + offset)
public inline fun <reified E : Enum<E>> E.previous(offset: Int = 1): E? = enumValues<E>().getOrNull(ordinal - offset)
