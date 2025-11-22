package tech.thatgravyboat.skyblockapi.helpers

actual object McIdentifier {
    actual typealias Identifier = net.minecraft.resources.Identifier

    actual fun fromNamespaceAndPath(namespace: String, path: String): Identifier = Identifier.fromNamespaceAndPath(namespace, path)
    actual fun withDefaultNamespace(path: String): Identifier = Identifier.withDefaultNamespace(path)
    actual fun parse(input: String): Identifier = Identifier.parse(input)
    actual fun tryParse(input: String): Identifier? = Identifier.tryParse(input)
}
