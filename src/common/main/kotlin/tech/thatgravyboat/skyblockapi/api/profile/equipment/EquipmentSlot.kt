package tech.thatgravyboat.skyblockapi.api.profile.equipment

import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockCategory
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import tech.thatgravyboat.skyblockapi.api.profile.items.equipment.EquipmentSlot as NewEquipmentSlot

@RemoveNextVersion
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

    companion object {
        fun fromNewEquipmentSlot(slot: NewEquipmentSlot): EquipmentSlot = when (slot) {
            NewEquipmentSlot.NECKLACE -> NECKLACE
            NewEquipmentSlot.CLOAK -> CLOAK
            NewEquipmentSlot.BELT -> BELT
            NewEquipmentSlot.GLOVES -> GLOVES
        }
    }

    fun toNewEquipmentSlot(): NewEquipmentSlot = when (this) {
        NECKLACE -> NewEquipmentSlot.NECKLACE
        CLOAK -> NewEquipmentSlot.CLOAK
        BELT -> NewEquipmentSlot.BELT
        GLOVES -> NewEquipmentSlot.GLOVES
    }

    internal val categories: Set<SkyBlockCategory> = categories.toSet()

    private val displayName = toFormattedName()

    override fun toString(): String = displayName
}
