package tech.thatgravyboat.skyblockapi.api.profile.equipment

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec

@GenerateCodec
data class EquipmentData(
    val slots: MutableMap<String, MutableMap<EquipmentSlot, ItemStack>> = mutableMapOf(),
    val riftSlots: MutableMap<String, MutableMap<EquipmentSlot, ItemStack>> = mutableMapOf()
) {
    companion object {
        val CODED = KCodec.getCodec<EquipmentData>()
    }
}
