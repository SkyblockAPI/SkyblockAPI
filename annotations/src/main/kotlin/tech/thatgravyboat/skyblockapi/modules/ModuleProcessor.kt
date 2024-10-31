package tech.thatgravyboat.skyblockapi.modules

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.writeTo

private val SKYBLOCK_API_TYPE = ClassName("tech.thatgravyboat.skyblockapi.api", "SkyBlockAPI")

class ModuleProcessor(
    private val generator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    private var ran = false

    private fun validateSymbol(symbol: KSAnnotated): KSClassDeclaration? {
        if (!symbol.validate()) {
            logger.warn("Symbol is not valid: $symbol")
            return null
        }

        if (symbol !is KSClassDeclaration || symbol.classKind != ClassKind.OBJECT) {
            logger.error("@Module is only valid on objects", symbol)
            return null
        }
        return symbol
    }

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (ran) return emptyList()
        ran = true

        val annotated = resolver.getSymbolsWithAnnotation(Module::class.qualifiedName!!).toList()
        val validModules = annotated.mapNotNull { validateSymbol(it) }

        logger.warn("--- Module Processor ---")
        logger.warn("Found ${validModules.size} modules")
        logger.warn("Generating Modules.kt")

        val file = FileSpec.builder("tech.thatgravyboat.skyblockapi.generated", "Modules")
            .indent("    ")
            .addType(
                TypeSpec.objectBuilder("Modules").apply {
                    this.addModifiers(KModifier.INTERNAL)
                    this.addFunction(
                        FunSpec.builder("load")
                            .addCode(
                                CodeBlock.builder()
                                    .addStatement("val bus = %T.eventBus", SKYBLOCK_API_TYPE)
                                    .addStatement("val isDev = tech.thatgravyboat.skyblockapi.helpers.McClient.isDev").apply {
                                        validModules.forEach { module ->
                                            val isDev = module.getAnnotationsByType(Module::class).first().devOnly
                                            if (isDev) {
                                                addStatement("if (isDev) bus.register(${module.qualifiedName!!.asString()})")
                                            } else {
                                                addStatement("bus.register(${module.qualifiedName!!.asString()})")
                                            }
                                        }
                                    }.build(),
                            )
                            .build(),
                    )
                }.build(),
            )

        file.build().writeTo(
            generator,
            Dependencies(true, *validModules.mapNotNull(KSClassDeclaration::containingFile).toTypedArray())
        )

        return emptyList()
    }
}

class ModuleProvider : SymbolProcessorProvider {
    override fun create(
        environment: SymbolProcessorEnvironment
    ): SymbolProcessor = ModuleProcessor(environment.codeGenerator, environment.logger)
}
