package tech.thatgravyboat.skyblockapi.api.profile.equipment

import tech.thatgravyboat.skyblockapi.api.data.SkyBlockCategory
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName

enum class EquipmentSlot(
    internal val slot: Int,
    vararg categories: SkyBlockCategory,
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
    ;

    internal val categories: Set<SkyBlockCategory> = categories.toSet()

    private val displayName = toFormattedName()

    override fun toString(): String = displayName
}
