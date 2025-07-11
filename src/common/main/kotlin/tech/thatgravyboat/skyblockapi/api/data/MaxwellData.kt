package tech.thatgravyboat.skyblockapi.api.data

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellPower
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellPowers
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellTuning
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
data class MaxwellData(
    var power: MaxwellPower = MaxwellPowers.NO_POWER,
    var magicalPower: Int = 0,
    val accessories: MutableList<ItemStack> = mutableListOf(),
    val unlockedPowers: MutableSet<MaxwellPower> = mutableSetOf(MaxwellPowers.NO_POWER),
    var tunings: MutableList<MaxwellTuning> = mutableListOf(),
) {
    companion object {
        val CODEC: Codec<MaxwellData> = SkyblockAPICodecs.getCodec<MaxwellData>()
    }
}
