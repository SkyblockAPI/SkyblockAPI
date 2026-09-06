package tech.thatgravyboat.skyblockapi.api.data.stored

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.profile.items.equipment.EquipmentData
import tech.thatgravyboat.skyblockapi.api.profile.items.equipment.EquipmentSlot
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.WardrobeSlot
import tech.thatgravyboat.skyblockapi.utils.extentions.isSameItem

internal object EquipmentStorage {

    private val EQUIPMENT = StoredProfileData(
        ::EquipmentData,
        EquipmentData.CODEC,
        "equipment.json",
    )

    val normalEquipment: MutableMap<EquipmentSlot, ItemStack>
        get() = EQUIPMENT.get()?.slots ?: emptyEquipment()

    val riftEquipment: MutableMap<EquipmentSlot, ItemStack>
        get() = EQUIPMENT.get()?.riftSlots ?: emptyEquipment()

    private fun emptyEquipment(): MutableMap<EquipmentSlot, ItemStack> =
        EquipmentSlot.entries.associateWith { ItemStack.EMPTY }.toMutableMap()

    val equipment: MutableMap<EquipmentSlot, ItemStack>
        get() = if (SkyBlockIsland.THE_RIFT.inIsland()) riftEquipment else normalEquipment

    fun setEquipment(slot: EquipmentSlot, item: ItemStack) {
        if (item.isSameItem(equipment[slot])) return
        equipment[slot] = item
        EQUIPMENT.save()
    }

    fun setEquipment(wardrobeSlot: WardrobeSlot?) {
        equipment[EquipmentSlot.NECKLACE] = wardrobeSlot?.slots[0] ?: ItemStack.EMPTY
        equipment[EquipmentSlot.CLOAK] = wardrobeSlot?.slots[1] ?: ItemStack.EMPTY
        equipment[EquipmentSlot.BELT] = wardrobeSlot?.slots[2] ?: ItemStack.EMPTY
        equipment[EquipmentSlot.GLOVES] = wardrobeSlot?.slots[3] ?: ItemStack.EMPTY
        EQUIPMENT.save()
    }
}
