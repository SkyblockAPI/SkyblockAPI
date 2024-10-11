package tech.thatgravyboat.skyblockapi.api.profile.equipment

import tech.thatgravyboat.skyblockapi.api.data.SkyBlockCategory

enum class EquipmentSlot(
    internal val slot: Int,
    internal vararg val categories: SkyBlockCategory,
) {
    NECKLACE(
        10,
        SkyBlockCategory.NECKLACE, SkyBlockCategory.DUNGEON_NECKLACE,
    ),
    CLOAK(
        19,
        SkyBlockCategory.CLOAK, SkyBlockCategory.DUNGEON_CLOAK,
    ),
    BELT(
        28,
        SkyBlockCategory.BELT, SkyBlockCategory.DUNGEON_BELT,
    ),
    GLOVES(
        37,
        SkyBlockCategory.GLOVES, SkyBlockCategory.DUNGEON_GLOVES,
        SkyBlockCategory.BRACELET, SkyBlockCategory.DUNGEON_BRACELET,
    ),
}
