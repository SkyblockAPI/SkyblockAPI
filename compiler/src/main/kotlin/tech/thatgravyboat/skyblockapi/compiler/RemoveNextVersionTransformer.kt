package tech.thatgravyboat.skyblockapi.compiler

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.IrGeneratorContext
import org.jetbrains.kotlin.ir.builders.buildStatement
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

@OptIn(UnsafeDuringIrConstructionAPI::class)
class RemoveNextVersionTransformer(
    val messageCollector: MessageCollector,
    val context: IrPluginContext,
    val message: String,
) : IrElementTransformerVoidWithContext() {

    val REMOVE_NEXT_VERSION: FqName = FqName("tech.thatgravyboat.skyblockapi.RemoveNextVersion")
    val DEPRECATED: ClassId = ClassId(FqName("kotlin"), Name.identifier("Deprecated"))

    override fun visitTypeAlias(declaration: IrTypeAlias): IrStatement {
        if (declaration.annotations.isEmpty()) return super.visitDeclaration(declaration)
        apply(declaration)
        return super.visitTypeAlias(declaration)
    }

    override fun visitDeclaration(declaration: IrDeclarationBase): IrStatement {
        if (declaration.annotations.isEmpty()) return super.visitDeclaration(declaration)
        apply(declaration)
        return super.visitDeclaration(declaration)
    }

    fun apply(declaration: IrMutableAnnotationContainer) {
        val constructor = declaration.annotations.firstOrNull { annotation ->
            if (annotation.type.classOrNull?.owner?.kotlinFqName != REMOVE_NEXT_VERSION) return@firstOrNull false
            if (declaration is IrDeclarationWithName && declaration is IrTypeAlias) {
                messageCollector.report(CompilerMessageSeverity.ERROR, "@RemoveNextVersion can not be applied to a type alias!")
            }
            true
        } ?: return
        val newConstructor = context.referenceClass(DEPRECATED)?.constructors?.firstOrNull() ?: return
        val newConst = declarationIrBuilder().irCallConstructor(
            newConstructor,
            listOf(),
        )
        newConstructor.owner.vParameters.forEach { e ->
            if (e.name.asString() == "message") {
                messageCollector.report(CompilerMessageSeverity.STRONG_WARNING, e.name.asString())
                newConst.arguments[0] = declarationIrBuilder().buildStatement(0, 0) { irString(message) }
            }
        }
        constructor.symbol.owner.vParameters.forEachIndexed { i, e ->
            newConstructor.owner.vParameters.find { it.name.asString() == e.name.asString() } ?: return@forEachIndexed
            val a = constructor.arguments[i] ?: return@forEachIndexed
            newConst.arguments[i] = a
            messageCollector.report(CompilerMessageSeverity.STRONG_WARNING, e.name.asString())

        }
        declaration.annotations += newConst
        declaration.annotations -= constructor
    }

    val IrFunction.vParameters get() = parameters.filter { it.kind == IrParameterKind.Regular || it.kind == IrParameterKind.Context }

    fun declarationIrBuilder(
        generatorContext: IrGeneratorContext = context,
        symbol: IrSymbol = currentScope!!.scope.scopeOwnerSymbol,
        startOffset: Int = UNDEFINED_OFFSET, endOffset: Int = UNDEFINED_OFFSET,
    ) = DeclarationIrBuilder(generatorContext, symbol, startOffset, endOffset)
}
