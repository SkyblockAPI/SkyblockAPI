package tech.thatgravyboat.skyblockapi.utils.extentions

import java.time.format.DateTimeFormatter
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.time.toJavaInstant

public fun currentInstant(): Instant = Clock.System.now()

public fun Duration.fromNow(): Instant = currentInstant() + this

public fun Duration.ago(): Instant = currentInstant() - this

public fun Instant.since(): Duration = currentInstant() - this

public fun Instant.until(): Duration = this - currentInstant()

public fun Instant.isInPast(): Boolean = this < currentInstant()
public fun Instant.isInFuture(): Boolean = this > currentInstant()

public fun DateTimeFormatter.format(instant: Instant): String = this.format(instant.toJavaInstant())
