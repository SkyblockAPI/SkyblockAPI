package tech.thatgravyboat.skyblockapi.utils.extentions

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import java.util.Optional

fun <O, A : Any> MapCodec<Optional<A>>.forNullGetter(getter: (O) -> A?): RecordCodecBuilder<O, Optional<A>> = this.forGetter {
    Optional.ofNullable(getter(it))
}
