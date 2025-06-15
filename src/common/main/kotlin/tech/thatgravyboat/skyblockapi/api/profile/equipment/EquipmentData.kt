package tech.thatgravyboat.skyblockapi.api.profile.equipment

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.RemoveNextVersion

@RemoveNextVersion
data class EquipmentData(
    val slots: MutableMap<EquipmentSlot, ItemStack> = mutableMapOf(),
    val riftSlots: MutableMap<EquipmentSlot, ItemStack> = mutableMapOf(),
)
