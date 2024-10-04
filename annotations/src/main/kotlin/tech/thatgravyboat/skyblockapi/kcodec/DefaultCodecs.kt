package tech.thatgravyboat.skyblockapi.kcodec

import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toTypeName

object DefaultCodecs {

    val codecs: MutableMap<TypeName, String> = mutableMapOf()
    private val stringCodecs: MutableSet<TypeName> = mutableSetOf()

    init {
        this.add("java.lang", "String", "com.mojang.serialization.Codec.STRING") { isString = true }
        this.add("java.lang", "Boolean", "com.mojang.serialization.Codec.BOOL")
        this.add("java.lang", "Byte", "com.mojang.serialization.Codec.BYTE")
        this.add("java.lang", "Short", "com.mojang.serialization.Codec.SHORT")
        this.add("java.lang", "Integer", "com.mojang.serialization.Codec.INT")
        this.add("java.lang", "Long", "com.mojang.serialization.Codec.LONG")
        this.add("java.lang", "Float", "com.mojang.serialization.Codec.FLOAT")
        this.add("java.lang", "Double", "com.mojang.serialization.Codec.DOUBLE")
        this.add("java.util", "UUID", "net.minecraft.core.UUIDUtil.STRING_CODEC") { isString = true }
        this.add("net.minecraft.resources", "ResourceLocation") { isString = true }

        this.add("net.minecraft.advancements.critereon", "BlockPredicate")
        this.add("net.minecraft.core", "BlockPos")
        this.add("net.minecraft.core", "GlobalPos")
        this.add("net.minecraft.core", "Vec3i")
        this.add("net.minecraft.core", "Vec3i")
        this.add("net.minecraft.nbt", "CompoundTag", "net.minecraft.nbt.TagParser.LENIENT_CODEC") { isString = true }
        this.add("net.minecraft.core", "Vec3i")
        this.add("org.joml", "Vector3f", "net.minecraft.util.ExtraCodecs.VECTOR3F")
        this.add("org.joml", "Vector4f", "net.minecraft.util.ExtraCodecs.VECTOR4F")
        this.add("com.mojang.authlib", "GameProfile", "net.minecraft.util.ExtraCodecs.GAME_PROFILE")
        this.add("net.minecraft.util", "ResourceLocationPattern")
        this.add("net.minecraft.util", "Unit")
        this.add("net.minecraft.util.random", "Weight")
        this.add("net.minecraft.util.valueproviders", "FloatProvider")
        this.add("net.minecraft.util.valueproviders", "IntProvider")

        this.add("net.minecraft.world.item", "Item", "net.minecraft.core.registries.BuiltInRegistries.ITEM.byNameCodec()") {
            isString = true
        }

        this.add("net.minecraft.world.item", "ItemStack")
    }

    fun isStringType(type: KSTypeReference): Boolean {
        if (Modifier.ENUM in type.modifiers) return true
        return stringCodecs.contains(type.resolve().toTypeName().copy(nullable = false))
    }

    private fun add(packageName: String, className: String, arguments: Arguments.() -> Unit = {}) {
        add(packageName, className, "$packageName.$className.CODEC", arguments)
    }

    private fun add(packageName: String, className: String, codec: String, arguments: Arguments.() -> Unit = {}) {
        codecs[ClassName(packageName, className)] = codec
        val args = Arguments().apply(arguments)
        if (args.isString) stringCodecs.add(ClassName(packageName, className))
    }

    private class Arguments {
        var isString: Boolean = false
    }

}
