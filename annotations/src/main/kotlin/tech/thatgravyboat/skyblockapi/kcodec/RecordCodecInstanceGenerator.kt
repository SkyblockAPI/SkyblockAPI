package tech.thatgravyboat.skyblockapi.kcodec

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ksp.toClassName
import tech.thatgravyboat.skyblockapi.kcodec.RecordCodecGenerator.Type

object RecordCodecInstanceGenerator {

    fun generateCodecInstance(code: CodeBlock.Builder, parameters: List<Pair<String, Type>>, declaration: KSClassDeclaration): Unit = with(code) {
        val defaults = parameters.filter { it.second == Type.DEFAULT }
        val normal = parameters.filter { it.second != Type.DEFAULT }

        if (defaults.isEmpty()) {
            add(
                "%T(${
                    normal.joinToString(", ") {
                        val name = it.first
                        if (it.second == Type.NULLABLE) "$name = p_$name.orElse(null)" else "$name = p_$name"
                    }
                })\n",
                declaration.toClassName(),
            )
        } else if (defaults.size > 6) {
            add(
                "var obj = %T(${
                    normal.joinToString(", ") {
                        val name = it.first
                        if (it.second == Type.NULLABLE) "$name = p_$name.orElse(null)" else "$name = p_$name"
                    }
                })\n",
                declaration.toClassName(),
            )
            for (pair in defaults) {
                val name = pair.first
                add("if (p_$name.isPresent) obj = obj.copy($name = p_$name.get())\n")
            }
            add("obj\n")
        } else {
            val possibilities = powerSet(defaults.map { it.first })

            add("when {\n")
            indent()

            for (defaultParams in possibilities) {
                if (defaultParams.isEmpty()) continue
                add(defaultParams.joinToString(" && ") { "p_$it.isPresent" })
                add(" -> %T(", declaration.toClassName())
                add(
                    normal.joinToString(", ") {
                        val name = it.first
                        if (it.second == Type.NULLABLE) "$name = p_$name.orElse(null)" else "$name = p_$name"
                    }
                )
                if (normal.isNotEmpty()) add(", ")
                add("${defaultParams.joinToString(", ") { "$it = p_$it.get()" }})\n")
            }
            add("else -> ")
            generateCodecInstance(code, normal, declaration)
            unindent()
            add("}\n")
        }
    }

    private fun <T> powerSet(originalSet: List<T>): List<List<T>> {
        return originalSet.fold(listOf(listOf())) { acc, element ->
            acc + acc.map { it + element }
        }
    }
}
