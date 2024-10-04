package tech.thatgravyboat.skyblockapi.kcodec

import com.squareup.kotlinpoet.ClassName

private const val API = "tech.thatgravyboat.skyblockapi.utils.codecs"
private const val SERIALIZATION = "com.mojang.serialization"
private const val DATAFIXER = "com.mojang.datafixers"

val CODEC_TYPE = ClassName(SERIALIZATION, "Codec")
val RECORD_CODEC_BUILDER_TYPE = ClassName("$SERIALIZATION.codecs", "RecordCodecBuilder")
val EITHER_TYPE = ClassName("$DATAFIXER.util", "Either")
val CODEC_UTILS_TYPE = ClassName(API, "CodecUtils")
val ENUM_CODEC_TYPE = ClassName(API, "EnumCodec")

val MUTABLE_MAP = ClassName("kotlin.collections", "MutableMap")
val MUTABLE_SET = ClassName("kotlin.collections", "MutableSet")
val MUTABLE_LIST = ClassName("kotlin.collections", "MutableList")
