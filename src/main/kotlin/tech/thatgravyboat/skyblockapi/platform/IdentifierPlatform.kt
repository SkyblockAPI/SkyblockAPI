package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey

public val ResourceKey<*>.identifier: Identifier get() = this.identifier()

public object Identifiers {

    public fun of(namespace: String, path: String): Identifier = Identifier.fromNamespaceAndPath(namespace, path)
    public fun of(path: String): Identifier = Identifier.withDefaultNamespace(path)

    public fun parse(id: String): Identifier? = Identifier.tryParse(id)
    public fun parse(namespace: String, path: String): Identifier? = Identifier.tryBuild(namespace, path)
    public fun parseWithSeparator(id: String, separator: Char): Identifier? = Identifier.tryBySeparator(id, separator)

    public fun isAllowedInIdentifier(c: Char): Boolean = Identifier.isAllowedInIdentifier(c)
}
