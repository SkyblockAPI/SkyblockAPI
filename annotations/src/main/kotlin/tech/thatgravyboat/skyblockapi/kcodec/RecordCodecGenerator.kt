package tech.thatgravyboat.skyblockapi.kcodec

import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import tech.thatgravyboat.skyblockapi.utils.CodeLineBuilder
import java.util.*

object RecordCodecGenerator {

    private const val MAX_PARAMETERS = 16

    private fun isValid(parameter: KSValueParameter, logger: KSPLogger, builtinCodecs: BuiltinCodecs): Boolean {
        val ksType = parameter.type.resolve()
        val name = parameter.name!!.asString()
        if (parameter.isVararg) {
            logger.error("parameter $name is a vararg")
        } else if (parameter.hasDefault && ksType.isMarkedNullable) {
            logger.error("parameter $name is nullable and has a default value")
        } else {
            val isMap = ksType.starProjection().toClassName() == Map::class.asClassName() || ksType.starProjection().toClassName() == MutableMap::class.asClassName()
            if (isMap) {
                val keyType = ksType.arguments.getRef(0)
                if (Modifier.ENUM !in keyType.modifiers && !builtinCodecs.isStringType(keyType.resolve().toTypeName().copy(false))) {
                    logger.error("parameter $name is a map with a key type that is not a string")
                    return false
                }
            }
            return true
        }
        return false
    }

    fun isValid(declaration: KSAnnotated?, logger: KSPLogger, builtinCodecs: BuiltinCodecs): Boolean {
        if (declaration !is KSClassDeclaration) {
            logger.error("Declaration is not a class")
        } else if (declaration.modifiers.contains(Modifier.INLINE)) {
            logger.error("@GenerateCodec can only be applied to non-inline classes")
        } else if (!declaration.isPublic()) {
            logger.error("@GenerateCodec can only be applied to public classes")
        } else if (Modifier.DATA !in declaration.modifiers) {
            logger.error("@GenerateCodec can only be applied to data classes")
        } else if (declaration.primaryConstructor == null) {
            logger.error("@GenerateCodec can only be applied to classes with a primary constructor")
        } else if (declaration.primaryConstructor!!.parameters.isEmpty()) {
            logger.error("@GenerateCodec can only be applied to classes with a primary constructor that has parameters")
        } else if (declaration.primaryConstructor!!.parameters.size > MAX_PARAMETERS) {
            logger.error(
                "@GenerateCodec can only be applied to classes with a primary constructor that has at most $MAX_PARAMETERS parameters",
            )
        } else if (!declaration.primaryConstructor!!.parameters.all { isValid(it, logger, builtinCodecs) }) {
            logger.error(
                "@GenerateCodec can only be applied to classes with a primary constructor that has valid parameters, view the error above for more information",
            )
        } else {
            return true
        }
        return false
    }

    private fun CodeLineBuilder.addCodec(type: KSType) {
        when (type.starProjection().toClassName()) {
            List::class.asClassName(), MUTABLE_LIST -> {
                add("%T.list(", CODEC_UTILS_TYPE)
                addCodec(type.arguments[0].type!!.resolve())
                add(")")
            }

            Set::class.asClassName(), MUTABLE_SET -> {
                add("%T.set(", CODEC_UTILS_TYPE)
                addCodec(type.arguments[0].type!!.resolve())
                add(")")
            }

            Map::class.asClassName(), MUTABLE_MAP -> {
                add("%T.map(", CODEC_UTILS_TYPE)
                addCodec(type.arguments[0].type!!.resolve())
                add(", ")
                addCodec(type.arguments[1].type!!.resolve())
                add(")")
            }

            EITHER_TYPE -> {
                add("%T.either(", CODEC_TYPE)
                addCodec(type.arguments[0].type!!.resolve())
                add(", ")
                addCodec(type.arguments[1].type!!.resolve())
                add(")")
            }

            else -> {
                add("getCodec<%T>()", type.toTypeName().copy(nullable = false))
            }
        }
    }

    private fun CodeBlock.Builder.createEntry(parameter: KSValueParameter, declaration: KSClassDeclaration): Pair<String, Type> {
        val name = parameter.name!!.asString()
        val nullable = parameter.type.resolve().isMarkedNullable
        val ksType = parameter.type.resolve()

        val builder = CodeLineBuilder()

        builder.addCodec(ksType)

        return when {
            parameter.hasDefault -> {
                builder.add(
                    ".optionalFieldOf(\"%L\").forGetter { getter -> %T.of(getter.%L) },\n",
                    name,
                    Optional::class.java,
                    name,
                )
                builder.build(this)
                name to Type.DEFAULT
            }

            nullable -> {
                builder.add(
                    ".optionalFieldOf(\"%L\").forGetter { getter -> %T.ofNullable(getter.%L) },\n",
                    name,
                    Optional::class.java,
                    name,
                )
                builder.build(this)
                name to Type.NULLABLE
            }

            else -> {
                builder.add(
                    ".fieldOf(\"%L\").forGetter(%T::%L),\n",
                    name,
                    declaration.toClassName(),
                    name,
                )
                builder.build(this)
                name to Type.NORMAL
            }
        }
    }

    fun generateCodec(declaration: KSAnnotated): PropertySpec {
        if (declaration !is KSClassDeclaration) {
            throw IllegalArgumentException("Declaration is not a class")
        }
        val codecName = declaration.simpleName.asString() + "Codec"
        return PropertySpec.builder(codecName, CODEC_TYPE.parameterizedBy(declaration.toClassName()))
            .addModifiers(KModifier.PRIVATE)
            .initializer(
                CodeBlock.builder().apply {
                    add("Codec.lazyInitialized {\n")
                    indent()
                    add("%T.create {\n", RECORD_CODEC_BUILDER_TYPE)
                    indent()
                    add("it.group(\n")
                    val args = mutableListOf<Pair<String, Type>>()

                    indent()
                    for (parameter in declaration.primaryConstructor!!.parameters) {
                        createEntry(parameter, declaration).let { args.add(it) }
                    }
                    unindent()

                    add(").apply(it) { ${args.joinToString(", ") { "p_${it.first}" }} -> \n")

                    indent()
                    add(
                        "var obj = %T(${
                            args.filter { it.second != Type.DEFAULT }.joinToString(", ") {
                                val name = it.first
                                if (it.second == Type.NULLABLE) "$name = p_$name.orElse(null)" else "$name = p_$name"
                            }
                        })\n",
                        declaration.toClassName(),
                    )
                    for (pair in args.filter { it.second == Type.DEFAULT }) {
                        val name = pair.first
                        add("if (p_$name.isPresent) obj = obj.copy($name = p_$name.get())\n")
                    }
                    add("obj\n")
                    unindent()

                    add("}\n")
                    unindent()
                    add("}\n")
                    unindent()
                    add("}\n")
                }.build(),
            )
            .build()
    }

    enum class Type {
        DEFAULT,
        NULLABLE,
        NORMAL
    }

    private fun List<KSTypeArgument>.getRef(index: Int): KSTypeReference = this[index].type!!

    private fun List<KSTypeArgument>.getType(index: Int): TypeName = getRef(index).resolve().toTypeName().copy(nullable = false)
}
