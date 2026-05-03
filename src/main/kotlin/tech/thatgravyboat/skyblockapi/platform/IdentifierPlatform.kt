package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey

val ResourceKey<*>.identifier: Identifier
    //? if > 1.21.10 {
    get() = this.identifier()
    //?} else
//get() = this.location()

object Identifiers {

    fun of(namespace: String, path: String): Identifier = Identifier.fromNamespaceAndPath(namespace, path)
    fun of(path: String): Identifier = Identifier.withDefaultNamespace(path)

    fun parse(id: String): Identifier? = Identifier.tryParse(id)
    fun parse(namespace: String, path: String): Identifier? = Identifier.tryBuild(namespace, path)
    fun parseWithSeparator(id: String, separator: Char): Identifier? = Identifier.tryBySeparator(id, separator)

    fun isAllowedInIdentifier(c: Char): Boolean =
        //? if > 1.21.10 {
        Identifier.isAllowedInIdentifier(c)
        //?} else
    //Identifier.isAllowedInResourceLocation(c)
}
