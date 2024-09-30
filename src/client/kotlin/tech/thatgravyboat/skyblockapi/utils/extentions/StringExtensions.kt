package tech.thatgravyboat.skyblockapi.utils.extentions

import java.text.NumberFormat
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private val formattedMultiplier = mapOf(
    "k" to 1_000L,
    "m" to 1_000_000L,
    "b" to 1_000_000_000L
)

private val romanNumerals = mapOf(
    'I' to 1,
    'V' to 5,
    'X' to 10,
    'L' to 50,
    'C' to 100,
    'D' to 500,
    'M' to 1000
)

internal fun String?.toIntValue(): Int = runCatching {
    this?.replace(",", "")?.toInt() ?: 0
}.getOrElse { 0 }

internal fun String?.toLongValue(): Long = runCatching {
    this?.replace(",", "")?.toLong() ?: 0
}.getOrElse { 0 }

internal fun String?.toFloatValue(): Float = runCatching {
    this?.replace(",", "")?.toFloat() ?: 0f
}.getOrElse { 0f }

internal fun String?.parseFormattedLong(): Long = runCatching {
    val commaless = this?.lowercase()?.replace(",", "")
    val multiplier = formattedMultiplier.entries.firstOrNull { commaless?.endsWith(it.key) == true }?.value
    return@runCatching if (multiplier != null) {
        commaless?.dropLast(1)?.toLong()?.times(multiplier) ?: 0
    } else {
        commaless?.toLong() ?: 0
    }
}.getOrElse { 0 }

internal fun String?.parseFormattedInt(): Int = parseFormattedLong().toInt()

internal fun String?.parseFormattedDouble(): Double = runCatching {
    val commaless = this?.lowercase()?.replace(",", "")
    val multiplier = formattedMultiplier.entries.firstOrNull { commaless?.endsWith(it.key) == true }?.value
    return@runCatching if (multiplier != null) {
        commaless?.dropLast(1)?.toDouble()?.times(multiplier) ?: 0.0
    } else {
        commaless?.toDouble() ?: 0.0
    }
}.getOrElse { 0.0 }

internal fun String?.parseFormattedFloat(): Float = parseFormattedDouble().toFloat()

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

internal fun String?.parseRomanNumeral(): Int = runCatching {
    var total = 0
    this?.forEachIndexed { index, c ->
        val value = romanNumerals[c] ?: return@forEachIndexed
        val nextValue = romanNumerals[this.getOrNull(index + 1)] ?: 0
        total += if (value < nextValue) -value else value
    }
    return@runCatching total
}.getOrElse { 0 }

fun Int.toFormattedString(): String = NumberFormat.getNumberInstance().format(this)
fun Long.toFormattedString(): String = NumberFormat.getNumberInstance().format(this)
fun Int.toRomanNumeral(): String {
    var number = this
    val roman = StringBuilder()
    romanNumerals.entries.reversed().forEach { (letter, value) ->
        while (number >= value) {
            roman.append(letter)
            number -= value
        }
    }
    return roman.toString()
}
