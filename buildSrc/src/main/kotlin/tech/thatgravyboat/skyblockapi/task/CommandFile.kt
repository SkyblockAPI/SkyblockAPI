package tech.thatgravyboat.skyblockapi.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import kotlin.io.path.writeText

fun createCommandClass(parameters: Int, isLast: Boolean): String = buildString {
    fun StringBuilder.forArguments(block: StringBuilder.(String) -> Unit) {
        repeat(parameters) {
            block(('a' + it).toString())
        }
    }

    fun StringBuilder.indent(additional: Int = 0) = apply {
        repeat(additional) {
            append("    ")
        }
    }
    val Self = "CommandBuilder$parameters"
    val Generics = buildString {
        forArguments {
            append(", ").append(it.uppercase())
        }
    }
    val SelfWithGenerics = buildString {
        append("$Self<CommandSender$Generics")
        append(">")
    }

    appendLine("@CommandClass")
    appendLine("class $SelfWithGenerics(")

    indent(1).appendLine("val name: String,")
    indent(1).appendLine("val context: CommandBuildContext,")
    forArguments {
        indent(1).appendLine("val ${it}ArgumentBinding: CommandArgumentBinding<${it.uppercase()}>,")
    }
    if (parameters != 0) {
        indent(1).appendLine("override val builder: ArgumentBuilder<CommandSender, *> = LiteralArgumentBuilder.literal(name)")
    }
    appendLine(") : CommandBuilder<CommandSender> {")
    if (parameters == 0) {
        indent(1).appendLine("override val builder: LiteralArgumentBuilder<CommandSender> = LiteralArgumentBuilder.literal(name)")
    }

    indent(1).appendLine("private val deferred: MutableList<CommandBuilder<CommandSender>> = mutableListOf()")

    appendLine()

    indent(1).appendLine("fun then(vararg name: String, builder: $SelfWithGenerics.() -> Unit) = name.forEach { it(builder) }")

    appendLine()

    indent(1).appendLine("operator fun String.invoke(builder: $SelfWithGenerics.() -> Unit): $SelfWithGenerics = this@$Self.literal(this) {")
    indent(2).append("$Self(this, context")
    forArguments {
        append(", ${it}ArgumentBinding")
    }
    appendLine(")")
    indent(1).appendLine("}.apply(builder)")

    appendLine()

    if (isLast) {
        indent(1).append("inline operator fun <reified Argument> String.invoke(argument: ArgumentType<Argument>, crossinline builder: CommandBuilder0<CommandSender>.(CommandBuildContext) -> Unit)")
        appendLine(": Nothing = TODO(\"Max depth reached!\")")
    } else {
        indent(1).appendLine("@JvmOverloads")
        indent(1).appendLine("inline fun <reified Argument> then(vararg name: String, argument: ArgumentType<Argument>, suggestionProvider: SuggestionProvider<CommandSender>? = null, crossinline builder: CommandBuilder${parameters + 1}<CommandSender$Generics, Argument>.(CommandBuildContext) -> Unit) = name.forEach { it(argument, suggestionProvider, builder) }")

        appendLine()

        indent(1).appendLine("@JvmOverloads")
        indent(1).append("inline operator fun <reified Argument> String.invoke(argument: ArgumentType<Argument>, suggestionProvider: SuggestionProvider<CommandSender>? = null, crossinline builder: CommandBuilder${parameters + 1}<CommandSender$Generics, Argument>.(CommandBuildContext) -> Unit)")

        appendLine("= this@$Self.argument(this, factory = {")
        indent(2).append("$Self(this, context")
        forArguments {
            append(", ${it}ArgumentBinding")
        }
        appendLine(")")
        indent(1).appendLine("}) {")
        indent(2).appendLine("CommandBuilder${parameters + 1}<CommandSender$Generics, Argument>(")
        indent(3).appendLine("name = this,")
        indent(3).appendLine("context,")
        forArguments {
            indent(3).appendLine("${it}ArgumentBinding,")
        }
        indent(3).appendLine("CommandArgumentBinding(this, Argument::class.java),")
        indent(3).appendLine("builder = RequiredArgumentBuilder.argument<CommandSender, Argument>(this, argument).apply {")
        indent(4).appendLine("if (suggestionProvider != null) suggests(suggestionProvider)")
        indent(3).appendLine("},")
        indent(2).appendLine(").apply { builder(context) }")
        indent(1).appendLine("}")
    }

    appendLine()
    indent(1).append("infix fun String.executes(callback: (${Generics.drop(2)}) -> Unit) = this { execute(callback) }")
    appendLine()

    indent(1).appendLine("fun execute(callback: (${Generics.drop(2)}) -> Unit) {")
    indent(2).appendLine("builder.executes {")


    forArguments {
        indent(3).appendLine("val $it = it.getArgument(${it}ArgumentBinding.name, ${it}ArgumentBinding.argument)")
    }
    indent(3).append("callback(")
    forArguments {
        if (it != "a") {
            append(", ")
        }
        append(it)
    }
    appendLine(")")

    indent(3).appendLine("Command.SINGLE_SUCCESS")
    indent(2).appendLine("}")
    indent(1).appendLine("}")

    appendLine()


    if (parameters == 0) {
        indent(1).appendLine("fun register(dispatcher: CommandDispatcher<CommandSender>) {")
        indent(1).appendLine("    deferred.forEach {")
        indent(1).appendLine("        builder.then(it.build())")
        indent(1).appendLine("    }")
        indent(1).appendLine("    dispatcher.register(builder)")
        indent(1).appendLine("}")
        appendLine()
    }

    indent(1).appendLine("override fun build(): ArgumentBuilder<CommandSender, *> {")
    indent(1).appendLine("    builder.arguments.clear()")
    indent(1).appendLine("    deferred.forEach {")
    indent(1).appendLine("        builder.then(it.build())")
    indent(1).appendLine("    }")
    indent(1).appendLine("    return builder")
    indent(1).appendLine("}")
    appendLine()

    indent(1).appendLine("override fun deferChild(node: CommandBuilder<CommandSender>) {")
    indent(1).appendLine("    deferred.add(node)")
    indent(1).appendLine("}")
    appendLine()


    appendLine("}")


}

fun createCommandFile(maxArguments: Int) = buildString {
    """
        import com.mojang.brigadier.Command
        import com.mojang.brigadier.CommandDispatcher
        import com.mojang.brigadier.arguments.ArgumentType
        import com.mojang.brigadier.builder.ArgumentBuilder
        import com.mojang.brigadier.builder.LiteralArgumentBuilder
        import com.mojang.brigadier.builder.RequiredArgumentBuilder
        import com.mojang.brigadier.context.CommandContext
        import com.mojang.brigadier.suggestion.SuggestionProvider
        import net.minecraft.commands.CommandBuildContext

        @DslMarker
        annotation class CommandClass

        interface CommandBuilder<CommandSender> {
            val builder: ArgumentBuilder<CommandSender, *>
            fun add(parent: CommandBuilder<CommandSender>) {
                parent.deferChild(this)
            }
            fun deferChild(node: CommandBuilder<CommandSender>)

            fun build(): ArgumentBuilder<CommandSender, *>

            fun <Type : CommandBuilder<CommandSender>> Type.literal(name: String, factory: String.() -> Type): Type {
                var result = this
                for (string in name.split(" ")) {
                    val previous = result
                    result = factory(string)
                    result.add(previous)
                }
                return result
            }

            fun <Type : CommandBuilder<CommandSender>, ArgumentType : CommandBuilder<CommandSender>> Type.argument(name: String, factory: String.() -> Type, argumentFactory: String.() -> ArgumentType): ArgumentType {
                var result = this
                val splitName = name.split(" ")
                for (string in splitName.dropLast(1)) {
                    val previous = result
                    result = factory(string)
                    result.add(previous)
                }
                val name = splitName.last()
                val argument = argumentFactory(name)
                argument.add(result)
                return argument
            }
        }

        data class CommandArgumentBinding<ArgumentType>(val name: String, val argument: Class<ArgumentType>)

        fun <CommandSender> CommandDispatcher<CommandSender>.command(name: String, buildContext: CommandBuildContext, init: CommandBuilder0<CommandSender>.() -> Unit) {
            val builder = CommandBuilder0<CommandSender>(name.substringBefore(' '), buildContext)
            val split = name.substringAfter(' ')
            if (split.isNotEmpty()) {
                builder.then(split, builder = init)
            } else {
                builder.init()
            }
            
            builder.register(this)
        }
    """.trimIndent().let(::appendLine)

    appendLine()

    repeat(maxArguments) {
        append(createCommandClass(it, it == maxArguments -1))
    }
}

@CacheableTask
abstract class CommandFileTask @Inject constructor(layout: ProjectLayout) : DefaultTask() {

    @get:Input
    abstract val maxDepth: Property<Int>

    @get:Input
    abstract val packageName: Property<String>

    @get:OutputDirectory
    abstract val output: RegularFileProperty


    init {
        maxDepth.convention(15)
        output.convention(layout.buildDirectory.file("generated/sbapi/commands"))
    }

    @TaskAction
    @OptIn(ExperimentalPathApi::class)
    fun generate() {
        val dir = output.get().asFile.toPath()
        if (dir.exists()) {
            dir.deleteRecursively()
            dir.deleteIfExists()
        }
        dir.createDirectories()
        val commands = dir.resolve("commands.kt")
        commands.writeText(buildString {
            append("package ").appendLine(packageName.get())
            appendLine()
            append(createCommandFile(maxDepth.get()))
        })
    }
}
