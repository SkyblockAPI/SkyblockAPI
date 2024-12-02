package tech.thatgravyboat.skyblockapi.utils.extentions

import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findGroup
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private val colorCodesStart = Regex("^(?<start>(§.| )*)(?!§.| )")
private val colorCodesEnd = Regex("^(?<end>(.§| )*)(?!.§| )")

private val formattedMultiplier = mapOf(
    "k" to 1_000L,
    "m" to 1_000_000L,
    "b" to 1_000_000_000L,
)

private val romanNumerals = mapOf(
    'I' to 1,
    'V' to 5,
    'X' to 10,
    'L' to 50,
    'C' to 100,
    'D' to 500,
    'M' to 1000,
)

internal fun String?.toIntValue(): Int = runCatching {
    this?.replace(",", "")?.toInt() ?: 0
}.getOrDefault(0)

internal fun String?.toLongValue(): Long = runCatching {
    this?.replace(",", "")?.toLong() ?: 0
}.getOrDefault(0)

internal fun String?.toFloatValue(): Float = runCatching {
    this?.replace(",", "")?.toFloat() ?: 0f
}.getOrDefault(0f)

internal fun String?.parseFormattedLong(): Long = runCatching {
    val commaless = this?.lowercase()?.replace(",", "")
    val multiplier = formattedMultiplier.entries.firstOrNull { commaless?.endsWith(it.key) == true }?.value
    return@runCatching if (multiplier != null) {
        commaless?.dropLast(1)?.toLong()?.times(multiplier) ?: 0
    } else {
        commaless?.toLong() ?: 0
    }
}.getOrDefault(0)

internal fun String?.parseFormattedInt(): Int = parseFormattedLong().toInt()

internal fun String?.parseFormattedDouble(): Double = runCatching {
    val commaless = this?.lowercase()?.replace(",", "")
    val multiplier = formattedMultiplier.entries.firstOrNull { commaless?.endsWith(it.key) == true }?.value
    return@runCatching if (multiplier != null) {
        commaless?.dropLast(1)?.toDouble()?.times(multiplier) ?: 0.0
    } else {
        commaless?.toDouble() ?: 0.0
    }
}.getOrDefault(0.0)

internal fun String?.parseFormattedFloat(): Float = parseFormattedDouble().toFloat()

internal fun String?.parseRomanOrArabic(): Int = parseRomanNumeral().takeIf { it != 0 } ?: toIntValue()

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

internal fun String?.parseWordDuration(): Duration? = runCatching {
    var total = 0L
    var current = ""
    this?.split(" ", ", ", " and ")?.forEach {
        if (it.toIntOrNull() != null) {
            current = it
        } else {
            val value = current.toLongOrNull() ?: 0L
            total += value * when (it.lowercase()) {
                "second", "seconds" -> 1
                "minute", "minutes" -> 60
                "hour", "hours" -> 60 * 60
                "day", "days" -> 60 * 60 * 24
                "week", "weeks" -> 60 * 60 * 24 * 7
                "month", "months" -> 60 * 60 * 24 * 30
                "year", "years" -> 60 * 60 * 24 * 365
                else -> 0
            }
            current = ""
        }
    }
    return@runCatching total.seconds
}.getOrNull()

internal fun String?.parseColonDuration(): Duration? = runCatching {
    val splits = this?.split(":") ?: return@runCatching null
    var currentMultiplier = (60.0.pow(splits.size - 1)).toLong()
    var total = 0L
    splits.forEach {
        total += it.toLong() * currentMultiplier.coerceAtLeast(1)
        currentMultiplier /= 60
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
}.getOrDefault(0)

internal fun <T : Enum<T>> Enum<T>.toFormattedName(): String =
    name.split("_").joinToString(" ") { it.lowercase().replaceFirstChar(Char::uppercase) }

private val regexGroup = Regexes.group("string")

private val cleanPlayerNameRegex = regexGroup.create(
    "clean.playername",
    "(?:(?<rank>\\[.+]) ?)?(?<name>[a-zA-Z0-9_]+)",
)

private val formattingCodesRegex = Regex("§.")

internal fun String.cleanPlayerName(): String {
    return cleanPlayerNameRegex.findGroup(this, "name") ?: this
}

fun Int.toFormattedString(): String = NumberFormat.getNumberInstance().format(this)
fun Long.toFormattedString(): String = NumberFormat.getNumberInstance().format(this)
fun Float.toFormattedString(): String = DecimalFormat.getNumberInstance().format(this)
fun Double.toFormattedString(): String = DecimalFormat.getNumberInstance().format(this)

private val thousandsPlace = listOf("", "M", "MM", "MMM")
private val hundreadsPlace = listOf("", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM")
private val tensPlace = listOf("", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC")
private val onesPlace = listOf("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX")

/**
 * @param subtractive This refers to if it should preform subtractions for numbers such as 4 if true it will be IV if false it will be IIII
 */
fun Int.toRomanNumeral(subtractive: Boolean = false): String {
    if (subtractive) {
        return thousandsPlace[this / 1000] + hundreadsPlace[this % 1000 / 100] + tensPlace[this % 100 / 10] + onesPlace[this % 10]
    } else {
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
}

fun String.stripColor(): String = formattingCodesRegex.replace(this, "")


fun String.trimIgnoreColor(): String {
    val start = colorCodesStart.find(this)?.groups?.get("start")?.value ?: ""
    val end = colorCodesEnd.find(this.reversed())?.groups?.get("end")?.value?.reversed() ?: ""
    val trimmed = this.removePrefix(start).removeSuffix(end)
    return start.replace(" ", "") + trimmed + end.replace(" ", "")
}
