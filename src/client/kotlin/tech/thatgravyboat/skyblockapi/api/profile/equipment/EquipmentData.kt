package tech.thatgravyboat.skyblockapi.api.profile.equipment

import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.generated.KCodec

@GenerateCodec
data class EquipmentData(
    val slots: MutableMap<EquipmentSlot, ItemStack> = mutableMapOf(),
    val riftSlots: MutableMap<EquipmentSlot, ItemStack> = mutableMapOf()
) {
    companion object {
        val CODEC = KCodec.getCodec<EquipmentData>()
    }
}
