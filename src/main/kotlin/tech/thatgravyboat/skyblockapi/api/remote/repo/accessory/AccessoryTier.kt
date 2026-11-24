@file:Suppress("JavaDefaultMethodsNotOverriddenByDelegation")

package tech.thatgravyboat.skyblockapi.api.remote.repo.accessory

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.IncludedCodec
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.generated.CodecUtils

// Not a data class so that equality checks aren't done with its contents
public class AccessoryTier internal constructor(
    public val items: Set<SkyBlockId>,
) : Set<SkyBlockId> by items, Comparable<AccessoryTier> {
    /** Index of the [AccessoryTier] in its [AccessoryFamily], gets initialized when family is created */
    public var tier: Int = -1
        internal set

    override fun compareTo(other: AccessoryTier): Int = tier.compareTo(other.tier)

    internal companion object {
        @IncludedCodec
        val CODEC: Codec<AccessoryTier> = CodecUtils.compactSet<SkyBlockId>().xmap(::AccessoryTier, AccessoryTier::items)
    }
}
