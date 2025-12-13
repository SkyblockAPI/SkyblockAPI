package tech.thatgravyboat.skyblockapi.utils.extentions


inline fun <reified T : Enum<T>> valueOfOrNull(name: String): T? = try { enumValueOf<T>(name) } catch (_: Throwable) { null }
