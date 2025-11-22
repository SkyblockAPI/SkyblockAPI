package tech.thatgravyboat.skyblockapi.helpers

import net.minecraft.resources.ResourceLocation

actual object McIdentifier {
    actual typealias Identifier = ResourceLocation

    actual fun fromNamespaceAndPath(namespace: String, path: String): Identifier = Identifier.fromNamespaceAndPath(namespace, path)
    actual fun withDefaultNamespace(path: String): Identifier = Identifier.withDefaultNamespace(path)
    actual fun parse(input: String): Identifier = Identifier.parse(input)
    actual fun tryParse(input: String): Identifier? = Identifier.tryParse(input)
}
