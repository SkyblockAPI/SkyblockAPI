package tech.thatgravyboat.skyblockapi.utils.extentions

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

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
    val multiplier = formattedMultiplier.entries.firstOrNull { commaless?.endsWith(it.key) == true }?.value
    return@runCatching if (multiplier != null) {
        commaless?.dropLast(1)?.toLong()?.times(multiplier) ?: 0
    } else {
        commaless?.toLong() ?: 0
    }
}.getOrElse { 0 }

internal fun String?.parseDuration(): Duration? = runCatching {
    var total = 0L
    var current = 0L
    this?.forEach {
        when {
            it.isDigit() -> current = current * 10 + it.toString().toLong()
            it.isLetter() -> {
                total += current * when (it) {
                    's' -> 1
                    'm' -> 60
                    'h' -> 60 * 60
                    'd' -> 60 * 60 * 24
                    else -> 0
                }
                current = 0
            }
        }
    }
    return@runCatching total.seconds
}.getOrNull()
