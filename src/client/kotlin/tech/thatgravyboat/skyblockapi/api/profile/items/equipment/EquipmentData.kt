package tech.thatgravyboat.skyblockapi.api.profile.items.equipment

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
data class EquipmentData(
    val slots: MutableMap<EquipmentSlot, ItemStack> = mutableMapOf(),
    val riftSlots: MutableMap<EquipmentSlot, ItemStack> = mutableMapOf(),
) {
    companion object {
        val CODEC: Codec<EquipmentData> = SkyblockAPICodecs.EquipmentDataCodec.codec()
    }
}
