package tech.thatgravyboat.skyblockapi.utils.extentions

import java.time.format.DateTimeFormatter
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.time.toJavaInstant

fun currentInstant(): Instant = Clock.System.now()

fun Duration.fromNow(): Instant = currentInstant() + this

fun Duration.ago(): Instant = currentInstant() - this

fun Instant.since(): Duration = currentInstant() - this

fun Instant.until(): Duration = this - currentInstant()

fun Instant.isInPast(): Boolean = this < currentInstant()
fun Instant.isInFuture(): Boolean = this > currentInstant()

fun DateTimeFormatter.format(instant: Instant): String = this.format(instant.toJavaInstant())
