package tech.thatgravyboat.skyblockapi.api.profile.equipment

import tech.thatgravyboat.skyblockapi.api.data.SkyblockCategory

enum class EquipmentSlot(
    internal val slot: Int,
    internal vararg val categories: SkyblockCategory
) {
    NECKLACE(
        10,
        SkyblockCategory.NECKLACE, SkyblockCategory.DUNGEON_NECKLACE
    ),
    CLOAK(
        19,
        SkyblockCategory.CLOAK, SkyblockCategory.DUNGEON_CLOAK
    ),
    BELT(
        28,
        SkyblockCategory.BELT, SkyblockCategory.DUNGEON_BELT
    ),
    GLOVES(
        37,
        SkyblockCategory.GLOVES, SkyblockCategory.DUNGEON_GLOVES,
        SkyblockCategory.BRACELET, SkyblockCategory.DUNGEON_BRACELET
    ),
}
