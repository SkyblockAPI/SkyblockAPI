package tech.thatgravyboat.skyblockapi.utils.codecs

import com.mojang.serialization.Codec


object CodecUtils {

    fun <T> set(codec: Codec<T>): Codec<Set<T>> = codec.listOf().xmap({ HashSet(it) }, { ArrayList(it) })
}
