@file:Suppress("JavaDefaultMethodsNotOverriddenByDelegation")

package tech.thatgravyboat.skyblockapi.api.remote.repo.accessory

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.Compact
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
public data class AccessoryFamily internal constructor(
    @FieldName("family") val name: String,
    @Compact val tiers: List<AccessoryTier>,
) : List<AccessoryTier> by tiers {

    init {
        // the tier of each accessory tier needs to get initialized here
        tiers.forEachIndexed { index, tier -> tier.tier = index }
    }

    inline val maxTier: Int get() = lastIndex
    public fun flatMapItems(): Sequence<SkyBlockId> = asSequence().flatMap(AccessoryTier::items)

    public fun tierOf(id: SkyBlockId): Int = indexOfFirst { id in it }
    public fun isMax(id: SkyBlockId): Boolean = lastOrNull()?.contains(id) == true
    public operator fun get(id: SkyBlockId): AccessoryTier? = find { id in it }

    public fun contains(id: SkyBlockId): Boolean = any { id in it }

    internal companion object {
        val CODEC: Codec<AccessoryFamily> = SkyblockAPICodecs.getCodec()
    }
}

