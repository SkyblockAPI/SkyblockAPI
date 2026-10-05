package tech.thatgravyboat.skyblockapi.utils.extentions


inline fun <reified T : Enum<T>> valueOfOrNull(name: String): T? = try { enumValueOf<T>(name) } catch (_: Throwable) { null }


inline fun <reified E : Enum<E>> E.nextCycling(offset: Int = 1): E = enumValues<E>().let { it[(ordinal + offset) % it.size] }
inline fun <reified E : Enum<E>> E.next(offset: Int = 1): E? = enumValues<E>().getOrNull(ordinal + offset)
inline fun <reified E : Enum<E>> E.previous(offset: Int = 1): E? = enumValues<E>().getOrNull(ordinal - offset)
