package tech.thatgravyboat.skyblockapi.helpers

expect object McIdentifier {
    interface Identifier

    fun fromNamespaceAndPath(namespace: String, path: String): Identifier
    fun withDefaultNamespace(path: String): Identifier
    fun parse(input: String): Identifier
    fun tryParse(input: String): Identifier?
}
