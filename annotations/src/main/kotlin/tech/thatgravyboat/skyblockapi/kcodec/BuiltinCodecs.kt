@file:OptIn(KspExperimental::class)

package tech.thatgravyboat.skyblockapi.kcodec

import com.google.devtools.ksp.*
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

class BuiltinCodecs {

    private val codecs: MutableMap<TypeName, Info> = mutableMapOf()

    init {
        // Add default ones

        this.add("java.lang", "String", "com.mojang.serialization.Codec.STRING", true)
        this.add("java.lang", "Boolean", "com.mojang.serialization.Codec.BOOL")
        this.add("java.lang", "Byte", "com.mojang.serialization.Codec.BYTE")
        this.add("java.lang", "Short", "com.mojang.serialization.Codec.SHORT")
        this.add("java.lang", "Integer", "com.mojang.serialization.Codec.INT")
        this.add("java.lang", "Long", "com.mojang.serialization.Codec.LONG")
        this.add("java.lang", "Float", "com.mojang.serialization.Codec.FLOAT")
        this.add("java.lang", "Double", "com.mojang.serialization.Codec.DOUBLE")
        this.add("java.util", "UUID", "net.minecraft.core.UUIDUtil.STRING_CODEC", true)
        this.add("net.minecraft.resources", "ResourceLocation", true)

        this.add("net.minecraft.advancements.critereon", "BlockPredicate")
        this.add("net.minecraft.core", "BlockPos")
        this.add("net.minecraft.core", "GlobalPos")
        this.add("net.minecraft.core", "Vec3i")
        this.add("net.minecraft.nbt", "CompoundTag", "net.minecraft.nbt.TagParser.LENIENT_CODEC", true)
        this.add("org.joml", "Vector3f", "net.minecraft.util.ExtraCodecs.VECTOR3F")
        this.add("org.joml", "Vector4f", "net.minecraft.util.ExtraCodecs.VECTOR4F")
        this.add("com.mojang.authlib", "GameProfile", "net.minecraft.util.ExtraCodecs.GAME_PROFILE")
        this.add("net.minecraft.util", "ResourceLocationPattern")
        this.add("net.minecraft.util", "Unit")
        this.add("net.minecraft.util.random", "Weight")
        this.add("net.minecraft.util.valueproviders", "FloatProvider")
        this.add("net.minecraft.util.valueproviders", "IntProvider")

        this.add(
            "net.minecraft.world.item", "Item",
            "net.minecraft.core.registries.BuiltInRegistries.ITEM.byNameCodec()",
            true
        )

        this.add("net.minecraft.world.item", "ItemStack", "net.minecraft.world.item.ItemStack.OPTIONAL_CODEC")
    }

    private fun add(packageName: String, className: String, keyable: Boolean = false) = add(ClassName(packageName, className), "$packageName.$className.CODEC", keyable)
    private fun add(packageName: String, className: String, codec: String, keyable: Boolean = false) = add(ClassName(packageName, className), codec, keyable)

    private fun add(type: TypeName, value: String, keyable: Boolean = false): Boolean {
        if (type !in codecs) {
            codecs[type] = Info(value, keyable)
            return true
        }
        return false
    }

    fun add(declaration: KSAnnotated, logger: KSPLogger) {
        if (isValid(declaration, logger)) {
            declaration as KSPropertyDeclaration
            val type = declaration.type.resolve().arguments[0].toTypeName()
            val isKeyable = declaration.getAnnotationsByType(IncludedCodec::class).first().keyable

            if (!add(type, declaration.qualifiedName!!.asString(), isKeyable)) {
                logger.error("Duplicate included codec for $type")
            }
        }
    }

    fun forEach(consumer: (TypeName, Info) -> Unit) {
        codecs.forEach(consumer)
    }

    fun isStringType(type: TypeName): Boolean {
        return codecs[type]?.keyable ?: false
    }

    companion object {
        fun isValid(declaration: KSAnnotated?, logger: KSPLogger): Boolean {
            if (declaration !is KSPropertyDeclaration) {
                logger.error("Declaration is not a property")
            } else if (!declaration.isPublic()) {
                logger.error("@IncludedCodec can only be applied to public properties")
            } else if (declaration.isLocal() || declaration.parentDeclaration == null || declaration.parentDeclaration !is KSClassDeclaration || (declaration.parentDeclaration as KSClassDeclaration).classKind != ClassKind.OBJECT) {
                logger.error("@IncludedCodec can only be applied to public properties in objects")
            } else if (!declaration.parentDeclaration!!.isPublic() && !declaration.parentDeclaration!!.isInternal()) {
                logger.error("@IncludedCodec can only be applied to public properties in public objects")
            } else if (declaration.type.resolve().starProjection().toClassName() != CODEC_TYPE) {
                logger.error("@IncludedCodec can only be applied to properties that are Codec<T>")
            } else {
                return true
            }
            return false
        }
    }
}

data class Info(
    val codec: String,
    val keyable: Boolean
)

