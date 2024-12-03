package tech.thatgravyboat.skyblockapi.utils.codecs

import com.mojang.serialization.Codec
import kotlinx.datetime.Instant
import tech.thatgravyboat.skyblockapi.kcodec.IncludedCodec
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal object ModCodecs {

    @IncludedCodec
    val INSTANT: Codec<Instant> = Codec.LONG.xmap(Instant.Companion::fromEpochMilliseconds, Instant::toEpochMilliseconds)

    @IncludedCodec
    val DURATION: Codec<Duration> = Codec.LONG.xmap({ it.milliseconds }, Duration::inWholeMilliseconds)

}
