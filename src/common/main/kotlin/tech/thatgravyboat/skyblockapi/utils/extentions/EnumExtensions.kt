package tech.thatgravyboat.skyblockapi.utils.extentions


inline fun <reified T : Enum<T>> valueOfOrNull(name: String): T? = enumValues<T>().firstOrNull { it.name == name }
