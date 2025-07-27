package tech.thatgravyboat.skyblockapi.utils.extentions

import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findGroup
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
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

fun String?.toIntValue(): Int = runCatching {
    this?.replace(",", "")?.toInt() ?: 0
}.getOrDefault(0)

fun String?.toLongValue(): Long = runCatching {
    this?.replace(",", "")?.toLong() ?: 0
}.getOrDefault(0)

fun String?.toFloatValue(): Float = runCatching {
    this?.replace(",", "")?.toFloat() ?: 0f
}.getOrDefault(0f)

fun String?.parseFormattedLong(default: Long = 0L): Long = parseFormattedDouble(default.toDouble()).toLong()

fun String?.parseFormattedInt(default: Int = 0): Int = parseFormattedLong(default.toLong()).toInt()

@JvmOverloads
fun String?.parseFormattedDouble(default: Double = 0.0): Double = runCatching {
    val commaless = this?.lowercase()?.replace(",", "")
    val multiplier = formattedMultiplier.entries.firstOrNull { commaless?.endsWith(it.key) == true }?.value
    return@runCatching if (multiplier != null) {
        commaless?.dropLast(1)?.toDouble()?.times(multiplier) ?: 0.0
    } else {
        commaless?.toDouble() ?: 0.0
    }
}.getOrDefault(default)

fun String?.parseFormattedFloat(): Float = parseFormattedDouble().toFloat()

fun String?.parseRomanOrArabic(): Int = parseRomanNumeral().takeIf { it != 0 } ?: toIntValue()

fun String?.parseDuration(): Duration? = runCatching {
    var total = 0L
    var current = 0L
    this?.forEach {
        when {
            it.isDigit() -> current = current * 10 + it.toString().toLong()
            it.isLetter() -> {
                total += current * when (it) {
                    't' -> 50
                    's' -> 1000
                    'm' -> 60 * 1000
                    'h' -> 60 * 60 * 1000
                    'd' -> 24 * 60 * 60 * 1000
                    else -> 0
                }
                current = 0
            }
        }
    }
    return@runCatching total.milliseconds
}.getOrNull()

fun String?.parseWordDuration(): Duration? = runCatching {
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

fun String?.parseColonDuration(): Duration? = runCatching {
    val splits = this?.split(":") ?: return@runCatching null
    var currentMultiplier = (60.0.pow(splits.size - 1)).toLong()
    var total = 0L
    splits.forEach {
        total += it.toLong() * currentMultiplier.coerceAtLeast(1)
        currentMultiplier /= 60
    }
    return@runCatching total.seconds
}.getOrNull()

fun String?.parseRomanNumeral(): Int = runCatching {
    var total = 0
    this?.forEachIndexed { index, c ->
        val value = romanNumerals[c] ?: return@forEachIndexed
        val nextValue = romanNumerals[this.getOrNull(index + 1)] ?: 0
        total += if (value < nextValue) -value else value
    }
    return@runCatching total
}.getOrDefault(0)

// todo: move into enum extensions with 1.21.6
fun <T : Enum<T>> Enum<T>.toFormattedName(): String = name.toTitleCase()

private val regexGroup = Regexes.group("string")

private val cleanPlayerNameRegex = regexGroup.create(
    "clean.playername",
    "(?:(?<rank>\\[.+]) ?)?(?<name>[a-zA-Z0-9_]+)",
)

private val formattingCodesRegex = Regex("§.")

fun String.cleanPlayerName(): String {
    return cleanPlayerNameRegex.findGroup(this, "name") ?: this
}

fun Number.toFormattedString(): String = NumberFormat.getNumberInstance().format(this)
fun Int.toFormattedString(): String = NumberFormat.getNumberInstance().format(this)
fun Long.toFormattedString(): String = NumberFormat.getNumberInstance().format(this)
fun Float.toFormattedString(): String = DecimalFormat.getNumberInstance().format(this)
fun Double.toFormattedString(): String = DecimalFormat.getNumberInstance().format(this)

private val thousandsPlace = listOf("", "M", "MM", "MMM")
private val hundredsPlace = listOf("", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM")
private val tensPlace = listOf("", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC")
private val onesPlace = listOf("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX")

/**
 * @param subtractive This refers to if it should preform subtractions for numbers such as 4 if true it will be IV if false it will be IIII
 */
fun Int.toRomanNumeral(subtractive: Boolean = false): String {
    if (subtractive) {
        return thousandsPlace[this / 1000] + hundredsPlace[this % 1000 / 100] + tensPlace[this % 100 / 10] + onesPlace[this % 10]
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

fun String.capitalize() = lowercase().split(" ", "_").joinToString(" ") { it.replaceFirstChar(Char::titlecase) }
fun String.toTitleCase() = capitalize()

fun String.trimIgnoreColor(): String {
    val start = colorCodesStart.find(this)?.groups?.get("start")?.value ?: ""
    val end = colorCodesEnd.find(this.reversed())?.groups?.get("end")?.value?.reversed() ?: ""
    val trimmed = this.removePrefix(start).removeSuffix(end)
    return start.replace(" ", "") + trimmed + end.replace(" ", "")
}

fun UUID.toDashlessString(): String = toString().replace("-", "")

private val screamingSnakeCaseRegex = "\\W+".toRegex()

fun String.toScreamingSnakeCase(): String = replace(screamingSnakeCaseRegex, "_").uppercase()

fun String.removeTrailingChar(target: Char): String = dropLastWhile {  it == target }

private val validChars = listOf(' ', '_', '-', ':')
fun String.sanitizeForCommandInput() = this.filter { it.isDigit() || it.isLetter() || it in validChars }.trim()
