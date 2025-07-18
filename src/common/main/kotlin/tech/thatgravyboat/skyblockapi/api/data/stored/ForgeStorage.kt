package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.items.forge.ForgeSlot
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.utils.codecs.CodecUtils
import tech.thatgravyboat.skyblockapi.utils.codecs.IncludedCodecs

internal object ForgeStorage {
    private val CODEC = CodecUtils.map(IncludedCodecs.INT_KEY, SkyblockAPICodecs.getCodec<ForgeSlot>())

    private val FORGE = StoredProfileData(
        { mutableMapOf() },
        CODEC,
        "forge.json",
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
