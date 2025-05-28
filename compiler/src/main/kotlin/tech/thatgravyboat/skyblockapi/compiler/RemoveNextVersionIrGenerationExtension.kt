package tech.thatgravyboat.skyblockapi.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

class RemoveNextVersionIrGenerationExtension(
    val messageCollector: MessageCollector,
    val message: String,
) : IrGenerationExtension {
    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext,
    ) {
        moduleFragment.transform(RemoveNextVersionTransformer(messageCollector, pluginContext, message), null)
    }
}
