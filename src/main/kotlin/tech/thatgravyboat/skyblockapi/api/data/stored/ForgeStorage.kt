package tech.thatgravyboat.skyblockapi.api.data.stored

import com.mojang.serialization.codecs.RecordCodecBuilder
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.items.forge.ForgeSlot
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.utils.codecs.CodecUtils
import tech.thatgravyboat.skyblockapi.utils.codecs.IncludedCodecs
import kotlin.time.Instant

internal object ForgeStorage {
    private val V0_CODEC = CodecUtils.map(
        IncludedCodecs.INT_KEY,
        RecordCodecBuilder.create {
            it.group(
                SkyblockAPICodecs.getCodec<SkyBlockId>().fieldOf("id").forGetter { it.skyBlockId },
                SkyblockAPICodecs.getCodec<Instant>().fieldOf("expiryTime").forGetter(ForgeSlot::expiryTime),
            ).apply(it) { item, time -> ForgeSlot(item, time) }
        },
    )

    private val V1_CODEC = CodecUtils.map(IncludedCodecs.INT_KEY, SkyblockAPICodecs.getCodec<ForgeSlot>())

    private val FORGE = StoredProfileData(
        version = 1,
        data = { mutableMapOf() },
        file = "forge.json",
        codec = {
            when (it) {
                0 -> V0_CODEC
                else -> V1_CODEC
            }
        },
    )

    val data get() = FORGE.get() ?: mutableMapOf()

    fun setSlot(slot: Int, forgeSlot: ForgeSlot) {
        FORGE.get()?.put(slot, forgeSlot)
        FORGE.save()
    }

    fun clearSlot(slot: Int) {
        FORGE.get()?.remove(slot)
        FORGE.save()
    }
}
