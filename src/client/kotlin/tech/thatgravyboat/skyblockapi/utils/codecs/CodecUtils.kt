package tech.thatgravyboat.skyblockapi.utils.codecs

import com.mojang.serialization.Codec


object CodecUtils {

    fun <T> set(codec: Codec<T>): Codec<Set<T>> = codec.listOf().xmap({ HashSet(it) }, { ArrayList(it) })

    fun <T> list(codec: Codec<T>): Codec<List<T>> = codec.listOf().xmap({ ArrayList(it) }, { it })

    fun <A, B> map(key: Codec<A>, value: Codec<B>): Codec<Map<A, B>> = Codec.unboundedMap(key, value).xmap({ it.toMutableMap() }, { it })
}
