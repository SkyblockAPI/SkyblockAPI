package tech.thatgravyboat.skyblockapi.utils.time
//? < 26.1 {
/*import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import java.time.format.DateTimeFormatter
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.time.toJavaInstant

@RemoveNextVersion(ReplaceWith("tech.thatgravyboat.skyblockapi.utils.extensions.currentInstant"))
fun currentInstant(): Instant = Clock.System.now()

@RemoveNextVersion(ReplaceWith("tech.thatgravyboat.skyblockapi.utils.extensions.fromNow"))
fun Duration.fromNow(): Instant = currentInstant() + this

@RemoveNextVersion(ReplaceWith("tech.thatgravyboat.skyblockapi.utils.extensions.ago"))
fun Duration.ago(): Instant = currentInstant() - this

@RemoveNextVersion(ReplaceWith("tech.thatgravyboat.skyblockapi.utils.extensions.since"))
fun Instant.since(): Duration = currentInstant() - this

@RemoveNextVersion(ReplaceWith("tech.thatgravyboat.skyblockapi.utils.extensions.until"))
fun Instant.until(): Duration = this - currentInstant()

@RemoveNextVersion(ReplaceWith("tech.thatgravyboat.skyblockapi.utils.extensions.format"))
fun DateTimeFormatter.format(instant: Instant): String = this.format(instant.toJavaInstant())
*///? }
