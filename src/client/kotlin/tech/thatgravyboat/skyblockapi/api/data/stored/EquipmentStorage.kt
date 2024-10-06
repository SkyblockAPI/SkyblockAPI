package tech.thatgravyboat.skyblockapi.api.data.stored

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.StoredData
import tech.thatgravyboat.skyblockapi.api.location.SkyblockIsland
import tech.thatgravyboat.skyblockapi.api.profile.equipment.EquipmentData
import tech.thatgravyboat.skyblockapi.api.profile.equipment.EquipmentSlot
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileAPI

internal object EquipmentStorage {

    private val EQUIPMENT = StoredData(
        EquipmentData(),
        EquipmentData.CODED,
        StoredData.defaultPath.resolve("equipment.json")
    )

    private val normalEquipment: MutableMap<EquipmentSlot, ItemStack>
        get() = EQUIPMENT.get().slots.getOrPut(ProfileAPI.profileName ?: "") {
            EquipmentSlot.entries.associateWith { ItemStack.EMPTY }.toMutableMap()
        }

    private val riftEquipment: MutableMap<EquipmentSlot, ItemStack>
        get() = EQUIPMENT.get().riftSlots.getOrPut(ProfileAPI.profileName ?: "") {
            EquipmentSlot.entries.associateWith { ItemStack.EMPTY }.toMutableMap()
        }

    val equipment: MutableMap<EquipmentSlot, ItemStack>
        get() = if (SkyblockIsland.THE_RIFT.inIsland()) riftEquipment else normalEquipment

    fun setEquipment(slot: EquipmentSlot, item: ItemStack) {
        if (item == equipment[slot]) return
        equipment[slot] = item
        EQUIPMENT.save()
    }

    fun getEquipment(slot: EquipmentSlot): ItemStack {
        return equipment[slot] ?: ItemStack.EMPTY
    }
}
