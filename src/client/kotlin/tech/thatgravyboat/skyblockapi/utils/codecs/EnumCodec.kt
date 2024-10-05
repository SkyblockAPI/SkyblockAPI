package tech.thatgravyboat.skyblockapi.utils.codecs

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps

class EnumCodec<T> private constructor(private val codec: Codec<T>) : Codec<T> {

    override fun <T1 : Any?> encode(input: T, ops: DynamicOps<T1>?, prefix: T1): DataResult<T1> = codec.encode(input, ops, prefix)
    override fun <T1 : Any?> decode(ops: DynamicOps<T1>?, input: T1): DataResult<Pair<T, T1>> = codec.decode(ops, input)

    companion object {

        fun <T : Enum<T>> of(constants: Array<T>): EnumCodec<T> =
            EnumCodec(Codec.withAlternative(constantCodec(constants), intCodec(constants)))

        internal fun <T> forKCodec(constants: Array<T>): EnumCodec<T> =
            EnumCodec(Codec.withAlternative(constantCodec(constants), intCodec(constants)))

        private fun <T> intCodec(constants: Array<T>): Codec<T> {
            return Codec.INT.flatXmap(
                { ordinal: Int ->
                    if (ordinal >= 0 && ordinal < constants.size) {
                        return@flatXmap DataResult.success<T>(constants[ordinal])
                    }
                    DataResult.error { "Unknown enum ordinal: $ordinal" }
                },
                { value: T -> DataResult.success((value as Enum<*>).ordinal) },
            )
        }

        private fun <T> constantCodec(constants: Array<T>): Codec<T> = Codec.STRING.flatXmap(
            { name: String ->
                runCatching {
                    DataResult.success(constants.first { (it as Enum<*>).name == name })
                }.getOrElse {
                    DataResult.error { "Unknown enum name: $name" }
                }
            },
            { value: T -> DataResult.success((value as Enum<*>).name) },
        )
    }

}
