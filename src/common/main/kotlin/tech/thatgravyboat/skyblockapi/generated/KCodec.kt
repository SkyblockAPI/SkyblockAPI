package tech.thatgravyboat.skyblockapi.generated

import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.RemoveNextVersion

@RemoveNextVersion
object KCodec {
    inline fun <reified T> getCodec(): Codec<T> = getCodec(T::class.java) as Codec<T>

    fun getCodec(clazz: Class<*>): Codec<*> = SkyblockAPICodecs.getCodec(clazz)
}
