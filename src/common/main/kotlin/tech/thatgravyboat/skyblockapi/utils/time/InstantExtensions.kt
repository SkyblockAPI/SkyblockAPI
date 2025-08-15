package tech.thatgravyboat.skyblockapi.utils.time

import java.time.format.DateTimeFormatter
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.time.jdk8.toJavaInstant

// TODO move this to extensions in 1.21.6 or 1.22.0

fun currentInstant(): Instant = Clock.System.now()

fun Duration.fromNow(): Instant = currentInstant() + this

fun Duration.ago(): Instant = currentInstant() - this

fun Instant.since(): Duration = currentInstant() - this

fun Instant.until(): Duration = this - currentInstant()

fun DateTimeFormatter.format(instant: Instant): String = this.format(instant.toJavaInstant())
