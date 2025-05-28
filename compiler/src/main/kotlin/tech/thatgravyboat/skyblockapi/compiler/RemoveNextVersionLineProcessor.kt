package tech.thatgravyboat.skyblockapi.compiler

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

internal val DEPRECATED_MESSAGE =
    CompilerConfigurationKey<String>(
        "The message to display in the deprecated annotation!",
    )

@AutoService(CommandLineProcessor::class)
@OptIn(ExperimentalCompilerApi::class)
class RemoveNextVersionLineProcessor : CommandLineProcessor {
    override val pluginId = "tech.thatgravyboat.skyblockapi"
    override val pluginOptions = listOf<AbstractCliOption>(OPTION_ANNOTATION_NAME)

    companion object {
        val OPTION_ANNOTATION_NAME =
            CliOption(
                optionName = "deprecatedMessage",
                valueDescription = "",
                description = DEPRECATED_MESSAGE.toString(),
                required = false,
                allowMultipleOccurrences = false,
            )
    }

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        when (option.optionName) {
            OPTION_ANNOTATION_NAME.optionName -> configuration.put(DEPRECATED_MESSAGE, value)
        }
    }
}
