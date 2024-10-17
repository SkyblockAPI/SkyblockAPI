package tech.thatgravyboat.skyblockapi.utils.time

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

internal fun currentInstant(): Instant = Clock.System.now()

internal fun Duration.fromNow(): Instant = currentInstant() + this

internal fun Duration.ago(): Instant = currentInstant() - this

internal fun Instant.since(): Duration = currentInstant() - this

internal fun Instant.until(): Duration = this - currentInstant()
