package tech.thatgravyboat.skyblockapi.api.data

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.Compact
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.FieldNames
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.OptionalBoolean
import me.owdding.ktcodecs.OptionalIfEmpty
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellPower
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellPowers
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellTuning
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellTuningTemplate
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.getSkyBlockId
import tech.thatgravyboat.skyblockapi.api.remote.repo.accessory.RepoAccessoryData
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.utils.extentions.filterKeysNotNull

// TODO: move somewhere else once its internal in all versions
@GenerateCodec
//~ if >= 26.4 'public' -> 'internal'
public data class MaxwellData(
    var power: MaxwellPower = MaxwellPowers.NO_POWER,
    @FieldNames("accessory_power", "magicalPower")
    var accessoryPower: Int = 0,
    //? if < 26.4
    @get:JvmName("getAccessoriesMap")
    val accessories: MutableMap</* Family */ String, ItemStack> = mutableMapOf(),
    @FieldNames("unlocked_power", "unlockedPowers")
    val unlockedPowers: MutableSet<MaxwellPower> = mutableSetOf(MaxwellPowers.NO_POWER),
    @OptionalIfEmpty @Compact
    var tunings: MutableSet<MaxwellTuning> = mutableSetOf(),
    @FieldName("tuning_templates") @OptionalIfEmpty @Compact
    var tuningTemplates: MutableList<MaxwellTuningTemplate> = mutableListOf(),
    //? if < 26.4 {
    @Deprecated("You can no longer consume it.") @FieldName("consumed_rift_prism") @OptionalBoolean(false)
    var consumedRiftPrism: Boolean = false,//?}
) {
    //? if < 26.4 {
    @Deprecated("Use the primary constructor instead", level = WARNING)
    public constructor(
        power: MaxwellPower,
        accessoryPower: Int,
        accessories: MutableList<ItemStack>,
        unlockedPowers: MutableSet<MaxwellPower> = mutableSetOf(MaxwellPowers.NO_POWER),
        tunings: MutableSet<MaxwellTuning> = mutableSetOf(),
        tuningTemplates: MutableList<MaxwellTuningTemplate> = mutableListOf(),
        @IntroducedAt("4.2.40")
        consumedRiftPrism: Boolean = false,
    ) : this(power, accessoryPower, accessories.associateBy { it.getSkyBlockId()?.let(RepoAccessoryData::getFamilyFromItemId)?.name }.filterKeysNotNull().toMutableMap(), unlockedPowers, tunings, tuningTemplates, consumedRiftPrism)

    @JvmName("component3")
    @Deprecated("Use accessories instead", level = HIDDEN)
    public fun oldComponent3(): MutableList<ItemStack> = accessories.values.toMutableList()

    @get:JvmName("getAccessories")
    @Deprecated("Use accessories instead", level = HIDDEN)
    val accessoryItems: MutableList<ItemStack> get() = accessories.values.toMutableList()

    @RemoveNextVersion
    @Deprecated("Use accessoryPower instead", ReplaceWith("accessoryPower"), level = HIDDEN)
    var magicalPower: Int by ::accessoryPower

    public fun copy(
        power: MaxwellPower,
        accessoryPower: Int,
        accessories: MutableList<ItemStack>,
        unlockedPowers: MutableSet<MaxwellPower> = this.unlockedPowers,
        tunings: MutableSet<MaxwellTuning> = this.tunings,
        tuningTemplates: MutableList<MaxwellTuningTemplate> = this.tuningTemplates,
        @IntroducedAt("4.2.40")
        consumedRiftPrism: Boolean = false,
    ): MaxwellData = MaxwellData(power, accessoryPower, accessories, unlockedPowers, tunings, tuningTemplates, consumedRiftPrism)
    //?}

    public companion object {
        public val CODEC: Codec<MaxwellData> = SkyblockAPICodecs.getCodec<MaxwellData>()
    }
}
