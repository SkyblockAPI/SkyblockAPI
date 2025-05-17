package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerType
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.slayer.SlayerData
import tech.thatgravyboat.skyblockapi.api.profile.slayer.SlayerEntry

internal object SlayerStorage {

    private val SLAYER = StoredProfileData(
        ::SlayerData,
        SlayerData.CODEC,
        "slayer.json",
    )

    val data: Map<SlayerType, SlayerEntry> get() = SLAYER.get()?.slayers ?: emptyMap()

    fun setData(key: SlayerType, data: SlayerEntry) {
        SLAYER.get()?.slayers[key] = data
        SLAYER.save()
    }

    fun setXp(key: SlayerType, xp: Long) {
        SLAYER.get()?.slayers?.computeIfAbsent(key) { SlayerEntry(xp) }?.xp = xp
        SLAYER.save()
    }

    fun setMeter(key: SlayerType, meter: Long) {
        SLAYER.get()?.slayers?.computeIfAbsent(key) { SlayerEntry(0L, meter) }?.meterXp = meter
        SLAYER.save()
    }

}
