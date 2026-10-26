package tech.thatgravyboat.skyblockapi.api.data

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.Compact
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.FieldNames
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.Lenient
import me.owdding.ktcodecs.OptionalBoolean
import me.owdding.ktcodecs.OptionalIfEmpty
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.AccessoryBagEntry
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellPower
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellPowers
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellTuning
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellTuningTemplate
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

// TODO: move somewhere else once its internal in all versions
@GenerateCodec
//~ if >= 26.4 'public' -> 'internal'
public data class MaxwellData(
    var power: MaxwellPower = MaxwellPowers.NO_POWER,
    @FieldNames("accessory_power", "magicalPower")
    var accessoryPower: Int = 0,
    //? < 26.4 {
    @FieldName("_")
    @OptionalIfEmpty
    @Suppress("PropertyName")
    @get:JvmName("getAccessories")
    val _accessories: MutableList<ItemStack> = mutableListOf(),
    //? }
    val unlockedPowers: MutableSet<MaxwellPower> = mutableSetOf(MaxwellPowers.NO_POWER),
    @OptionalIfEmpty @Compact
    var tunings: MutableSet<MaxwellTuning> = mutableSetOf(),
    @FieldName("tuning_templates") @OptionalIfEmpty @Compact
    var tuningTemplates: MutableList<MaxwellTuningTemplate> = mutableListOf(),
    @FieldName("consumed_rift_prism") @OptionalBoolean(false) @IntroducedAt("4.2.40")
    var consumedRiftPrism: Boolean = false,
    @Lenient @get:JvmName("accessories") @IntroducedAt("4.2.42")
    internal val accessories: MutableList<AccessoryBagEntry> = mutableListOf()
) {

    @RemoveNextVersion
    @Deprecated("Use accessoryPower instead", ReplaceWith("accessoryPower"), level = HIDDEN)
    var magicalPower: Int by ::accessoryPower

    public companion object {
        public val CODEC: Codec<MaxwellData> = SkyblockAPICodecs.getCodec<MaxwellData>()
    }
}
