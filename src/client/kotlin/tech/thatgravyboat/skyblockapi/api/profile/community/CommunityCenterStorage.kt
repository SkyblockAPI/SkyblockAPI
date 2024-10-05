package tech.thatgravyboat.skyblockapi.api.profile.community

import tech.thatgravyboat.skyblockapi.api.data.StoredData
import tech.thatgravyboat.skyblockapi.api.profile.FameRank
import tech.thatgravyboat.skyblockapi.api.profile.FameRanks
import java.util.*

internal object CommunityCenterStorage {
    private val COMMUNITY_CENTER = StoredData(
        CommunityCenterData(),
        CommunityCenterData.CODEC,
        StoredData.defaultPath.resolve("community_center_data.json"),
    )

    fun getRank(uuid: UUID): FameRank? = COMMUNITY_CENTER.get().ranks[uuid]?.let { FameRanks.getByName(it) }

    fun setRank(uuid: UUID, rank: FameRank?) {
        rank ?: return
        if (rank == getRank(uuid)) return
        COMMUNITY_CENTER.get().ranks[uuid] = rank.name
        COMMUNITY_CENTER.save()
    }

    fun getBitsAvailable(profile: String): Long = COMMUNITY_CENTER.get().bitsAvailable[profile] ?: 0

    fun setBitsAvailable(profile: String, bits: Long) {
        if (bits == getBitsAvailable(profile)) return
        COMMUNITY_CENTER.get().bitsAvailable[profile] = bits
        COMMUNITY_CENTER.save()
    }
}
