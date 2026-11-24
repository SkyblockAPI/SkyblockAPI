package tech.thatgravyboat.skyblockapi.utils.codecs

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec

public object CodecUtils {

    public fun <T> set(codec: Codec<T>): Codec<MutableSet<T>> =
        codec.listOf().xmap({ it.toMutableSet() }, { it.toList() })

    public fun <T> list(codec: Codec<T>): Codec<MutableList<T>> =
        codec.listOf().xmap({ it.toMutableList() }, { it })

    public fun <A, B> map(key: Codec<A>, value: Codec<B>): Codec<MutableMap<A, B>> =
        Codec.unboundedMap(key, value).xmap({ it.toMutableMap() }, { it })

    public fun <T> unit(value: () -> T): Codec<T> = MapCodec.unitCodec(value)
}
