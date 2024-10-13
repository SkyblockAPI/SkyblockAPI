package tech.thatgravyboat.skyblockapi.utils.codecs

import com.mojang.serialization.Codec
import kotlinx.datetime.Instant

internal object ModCodecs {

    val INSTANT: Codec<Instant> = Codec.LONG.xmap(Instant.Companion::fromEpochMilliseconds, Instant::toEpochMilliseconds)

}
