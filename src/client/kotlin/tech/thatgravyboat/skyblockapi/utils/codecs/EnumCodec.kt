package tech.thatgravyboat.skyblockapi.utils.codecs

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps

class EnumCodec<T : Enum<T>> private constructor(private val codec: Codec<T>) : Codec<T> {

    constructor(enumClass: Class<T>) : this(Codec.withAlternative(constantCodec(enumClass), intCodec(enumClass)))

    override fun <T1 : Any?> encode(input: T, ops: DynamicOps<T1>?, prefix: T1): DataResult<T1> = codec.encode(input, ops, prefix)
    override fun <T1 : Any?> decode(ops: DynamicOps<T1>?, input: T1): DataResult<Pair<T, T1>> = codec.decode(ops, input)

    companion object {

        private fun <T : Enum<T>> intCodec(enumClass: Class<T>): Codec<T> {
            return Codec.INT.flatXmap(
                { ordinal: Int ->
                    val values = enumClass.enumConstants
                    if (ordinal >= 0 && ordinal < values.size) {
                        return@flatXmap DataResult.success<T>(values[ordinal])
                    }
                    DataResult.error { "Unknown enum ordinal: $ordinal" }
                },
                { value: T -> DataResult.success(value.ordinal) },
            )
        }

        private fun <T : Enum<T>> constantCodec(enumClass: Class<T>): Codec<T> = Codec.STRING.flatXmap(
            { name: String ->
                runCatching {
                    DataResult.success(enumClass.enumConstants.first { it.name == name })
                }.getOrElse {
                    DataResult.error { "Unknown enum name: $name" }
                }
            },
            { value: T -> DataResult.success(value.name) },
        )
    }

}
