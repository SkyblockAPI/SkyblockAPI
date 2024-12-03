package tech.thatgravyboat.skyblockapi.kcodec

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class GenerateCodec

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.PROPERTY)
annotation class IncludedCodec(

    /**
     * Keyable means it can be used in a key of a map meaning its serialized to a string
     */
    val keyable: Boolean = false
)
