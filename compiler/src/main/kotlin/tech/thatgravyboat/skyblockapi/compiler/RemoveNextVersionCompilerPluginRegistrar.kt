package tech.thatgravyboat.skyblockapi.compiler

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.messageCollector

@AutoService(CompilerPluginRegistrar::class)
@OptIn(ExperimentalCompilerApi::class)
class RemoveNextVersionCompilerPluginRegistrar : CompilerPluginRegistrar() {
    override val supportsK2 = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val message = configuration.get(DEPRECATED_MESSAGE) ?: run {
            configuration.messageCollector.report(CompilerMessageSeverity.ERROR, "No message set!")
            return
        }

        IrGenerationExtension.registerExtension(RemoveNextVersionIrGenerationExtension(configuration.messageCollector, message))
    }
}
