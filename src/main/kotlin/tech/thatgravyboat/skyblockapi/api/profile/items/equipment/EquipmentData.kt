package tech.thatgravyboat.skyblockapi.api.profile.items.equipment

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

// TODO: move somewhere else once its internal in all versions
@GenerateCodec
//~ if >= 26.4 'public' -> 'internal'
internal data class EquipmentData(
    val slots: MutableMap<EquipmentSlot, ItemStack> = mutableMapOf(),
    val riftSlots: MutableMap<EquipmentSlot, ItemStack> = mutableMapOf(),
) {
    public companion object {
        internal val CODEC: Codec<EquipmentData> = SkyblockAPICodecs.EquipmentDataCodec.codec()
    }
}
