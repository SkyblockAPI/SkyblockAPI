package tech.thatgravyboat.skyblockapi.utils.time

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

fun currentInstant(): Instant = Clock.System.now()

fun Duration.fromNow(): Instant = currentInstant() + this

fun Duration.ago(): Instant = currentInstant() - this

fun Instant.since(): Duration = currentInstant() - this

fun Instant.until(): Duration = this - currentInstant()
