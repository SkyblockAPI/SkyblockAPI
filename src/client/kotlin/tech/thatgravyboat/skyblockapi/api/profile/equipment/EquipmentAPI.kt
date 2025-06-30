package tech.thatgravyboat.skyblockapi.api.profile.equipment

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.profile.items.equipment.EquipmentAPI as NewEquipmentAPI

@RemoveNextVersion
object EquipmentAPI {
    val equipment: Map<EquipmentSlot, ItemStack>
        get() = NewEquipmentAPI.islandEquipment.map { (key, value) -> EquipmentSlot.fromNewEquipmentSlot(key) to value }.toMap()

    fun getEquipment(slot: EquipmentSlot): ItemStack = equipment[slot] ?: ItemStack.EMPTY
}
