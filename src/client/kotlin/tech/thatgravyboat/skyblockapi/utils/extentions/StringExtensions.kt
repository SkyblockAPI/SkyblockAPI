package tech.thatgravyboat.skyblockapi.utils.extentions

private val formattedMultiplier = mapOf(
    "k" to 1_000L,
    "m" to 1_000_000L,
    "b" to 1_000_000_000L
)

internal fun String?.toIntValue(): Int = runCatching {
    this?.replace(",", "")?.toInt() ?: 0
}.getOrElse { 0 }

internal fun String?.parseFormattedLong(): Long = runCatching {
    val commaless = this?.replace(",", "")
    val multiplier = formattedMultiplier.keys.firstOrNull { commaless?.endsWith(it) == true }
    val value = commaless?.substringBefore(multiplier ?: "")?.toLong() ?: 0
    return value * (formattedMultiplier[multiplier] ?: 1)
}.getOrElse { 0 }
