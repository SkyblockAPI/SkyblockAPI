package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey

val ResourceKey<*>.identifier: Identifier get() = this.identifier()

object Identifiers {

    fun of(namespace: String, path: String): Identifier = Identifier.fromNamespaceAndPath(namespace, path)
    fun of(path: String): Identifier = Identifier.withDefaultNamespace(path)

    fun parse(id: String): Identifier? = Identifier.tryParse(id)
    fun parse(namespace: String, path: String): Identifier? = Identifier.tryBuild(namespace, path)
    fun parseWithSeparator(id: String, separator: Char): Identifier? = Identifier.tryBySeparator(id, separator)

    fun isAllowedInIdentifier(c: Char): Boolean = Identifier.isAllowedInIdentifier(c)
}
