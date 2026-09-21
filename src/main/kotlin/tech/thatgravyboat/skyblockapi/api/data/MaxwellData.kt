package tech.thatgravyboat.skyblockapi.api.data

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.Compact
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.FieldNames
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.OptionalIfEmpty
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellPower
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellPowers
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellTuning
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellTuningTemplate
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
data class MaxwellData(
    var power: MaxwellPower = MaxwellPowers.NO_POWER,
    @FieldNames("accessory_power", "magicalPower")
    var accessoryPower: Int = 0,
    val accessories: MutableList<ItemStack> = mutableListOf(),
    val unlockedPowers: MutableSet<MaxwellPower> = mutableSetOf(MaxwellPowers.NO_POWER),
    @OptionalIfEmpty @Compact
    var tunings: MutableSet<MaxwellTuning> = mutableSetOf(),
    @FieldName("tuning_templates") @OptionalIfEmpty @Compact
    var tuningTemplates: MutableList<MaxwellTuningTemplate> = mutableListOf(),
) {
    @RemoveNextVersion
    @Deprecated("Use accessoryPower instead", ReplaceWith("accessoryPower"), level = HIDDEN)
    var magicalPower: Int by ::accessoryPower

    companion object {
        val CODEC: Codec<MaxwellData> = SkyblockAPICodecs.getCodec<MaxwellData>()
    }
}
