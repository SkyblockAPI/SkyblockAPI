#!/usr/bin/env kotlin

import java.nio.file.Path
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.readText
import kotlin.system.exitProcess

fun Path.executeCommand(vararg args: String) {
    ProcessBuilder(*args)
        .redirectOutput(this.toFile())
        .directory(this.parent.toFile())
        .start()
        .waitFor()
}

val failed = mutableListOf<String>()

__FILE__.toPath().toAbsolutePath().parent.parent.parent.resolve("versions").listDirectoryEntries().forEach {
    val diff = it.resolve("diff")
    diff.executeCommand("git", "diff", "HEAD", "api/${it.fileName}.api")
    val content = diff.readText()
    val lines = content.substringAfter("@@").substringAfter('\n').lines()

    val additions = lines.filter { it.startsWith("+") }
    val removals = lines.filter { it.startsWith("-") }
    failed.addAll(removals.filterNot { it.startsWith("-\tprivate") }.filterNot {
        additions.contains(it.replaceFirst('-', '+')) || additions.contains(it.replace(Regex("-\t(.*?) (fun|field)"), "+\t$1 synthetic $2"))
    })
}

if (failed.isNotEmpty()) {
    failed.distinct().forEach {
        println(it)
    }
}
