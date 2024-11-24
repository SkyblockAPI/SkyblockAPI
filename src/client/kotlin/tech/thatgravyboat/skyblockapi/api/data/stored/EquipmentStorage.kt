package tech.thatgravyboat.skyblockapi.api.data.stored

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.profile.equipment.EquipmentData
import tech.thatgravyboat.skyblockapi.api.profile.equipment.EquipmentSlot

internal object EquipmentStorage {

    private val EQUIPMENT = StoredProfileData(
        ::EquipmentData,
        EquipmentData.CODEC,
        "equipment.json",
    )

    private val normalEquipment: MutableMap<EquipmentSlot, ItemStack>
        get() = EQUIPMENT.get()?.slots ?: emptyEquipment()

    private val riftEquipment: MutableMap<EquipmentSlot, ItemStack>
        get() = EQUIPMENT.get()?.riftSlots ?: emptyEquipment()

    private fun emptyEquipment(): MutableMap<EquipmentSlot, ItemStack> =
        EquipmentSlot.entries.associateWith { ItemStack.EMPTY }.toMutableMap()

    val equipment: MutableMap<EquipmentSlot, ItemStack>
        get() = if (SkyBlockIsland.THE_RIFT.inIsland()) riftEquipment else normalEquipment

    fun setEquipment(slot: EquipmentSlot, item: ItemStack) {
        if (item == equipment[slot]) return
        equipment[slot] = item
        EQUIPMENT.save()
    }
}
