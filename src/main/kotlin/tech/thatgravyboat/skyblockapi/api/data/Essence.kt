package tech.thatgravyboat.skyblockapi.api.data

enum class Essence(val canBeSold: Boolean = true) {
    WITHER,
    UNDEAD,
    DRAGON,
    SPIDER,
    ICE,
    DIAMOND,
    GOLD,
    CRIMSON,
    SUN_GECKO(false),
    FOREST,
    ;

    val bazaarId: String? = "ESSENCE_${this.name}".takeIf { canBeSold }
}
