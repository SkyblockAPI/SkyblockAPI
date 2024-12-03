package tech.thatgravyboat.skyblockapi.kcodec

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

class KCodecProcessor(
    private val generator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    private var ran = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (ran) return emptyList()
        ran = true

        val builtinCodecs = BuiltinCodecs()
        resolver.getSymbolsWithAnnotation(IncludedCodec::class.qualifiedName!!).forEach { builtinCodecs.add(it, logger) }

        val annotated = resolver.getSymbolsWithAnnotation(GenerateCodec::class.qualifiedName!!).toList()
        val validGeneratedCodecs = annotated.filter { RecordCodecGenerator.isValid(it, logger, builtinCodecs) }
        val generatedCodecs = validGeneratedCodecs.map { RecordCodecGenerator.generateCodec(it) }

        val file = FileSpec.builder("tech.thatgravyboat.skyblockapi.generated", "KCodec")
            .indent("    ")
            .addType(
                TypeSpec.objectBuilder("KCodec").apply {
                    this.addProperties(generatedCodecs)

                    this.addFunction(
                        FunSpec.builder("getCodec").apply {
                            this.addModifiers(KModifier.INLINE)
                            this.addTypeVariable(TypeVariableName("T").copy(reified = true))
                            this.returns(
                                ClassName("com.mojang.serialization", "Codec")
                                    .parameterizedBy(TypeVariableName("T")),
                            )
                            this.addCode("return getCodec(T::class.java) as Codec<T>")
                        }.build(),
                    )

                    this.addFunction(
                        FunSpec.builder("getCodec").apply {
                            this.addParameter("clazz", ClassName("java.lang", "Class").parameterizedBy(STAR))
                            this.returns(ClassName("com.mojang.serialization", "Codec").parameterizedBy(STAR))
                            this.addCode("return when {\n")
                            builtinCodecs.forEach { type, info ->
                                this.addCode("    clazz == %T::class.java -> ${info.codec}\n", type)
                            }
                            this.addCode("    clazz.isEnum -> %T.forKCodec(clazz.enumConstants)\n", ENUM_CODEC_TYPE)
                            for (codec in validGeneratedCodecs) {
                                this.addCode(
                                    "    clazz == %T::class.java -> %L\n",
                                    (codec as KSClassDeclaration).toClassName(),
                                    "${codec.simpleName.asString()}Codec",
                                )
                            }
                            this.addCode("    else -> throw IllegalArgumentException(\"Unknown codec for class: \$clazz\")\n")
                            this.addCode("}\n")
                        }.build(),
                    )

                }.build(),
            )

        file.build().writeTo(generator, Dependencies(true, *annotated.mapNotNull { it.containingFile }.toTypedArray()))

        return emptyList()
    }

}

class KCodecProvider : SymbolProcessorProvider {
    override fun create(
        environment: SymbolProcessorEnvironment
    ): SymbolProcessor = KCodecProcessor(environment.codeGenerator, environment.logger)
}
