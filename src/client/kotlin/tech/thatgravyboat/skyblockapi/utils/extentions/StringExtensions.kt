package tech.thatgravyboat.skyblockapi.utils.extentions

internal fun String?.toIntValue(): Int = runCatching {
    this?.replace(",", "")?.toInt() ?: 0
}.getOrElse { 0 }
