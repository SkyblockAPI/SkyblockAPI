package tech.thatgravyboat.skyblockapi.utils.codecs

import com.mojang.serialization.Codec


object CodecUtils {

    fun <T> set(codec: Codec<T>): Codec<MutableSet<T>> =
        codec.listOf().xmap({ it.toMutableSet() }, { it.toList() })

    fun <T> list(codec: Codec<T>): Codec<MutableList<T>> =
        codec.listOf().xmap({ it.toMutableList() }, { it })

    fun <A, B> map(key: Codec<A>, value: Codec<B>): Codec<MutableMap<A, B>> =
        Codec.unboundedMap(key, value).xmap({ it.toMutableMap() }, { it })
}
