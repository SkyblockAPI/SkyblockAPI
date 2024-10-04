package tech.thatgravyboat.skyblockapi.api.data

import tech.thatgravyboat.skyblockapi.api.profile.CommunityCenterData
import tech.thatgravyboat.skyblockapi.api.profile.FameRank
import java.util.*

object CommunityCenterStorage {
    private val COMMUNITY_CENTER = StoredData(
        CommunityCenterData(),
        CommunityCenterData.CODEC,
        StoredData.defaultPath.resolve("community_center_data.json"),
    )

    fun getRank(uuid: UUID): FameRank? = COMMUNITY_CENTER.get().ranks[uuid]

    fun setRank(uuid: UUID, rank: FameRank?) {
        rank ?: return
        COMMUNITY_CENTER.get().ranks[uuid] = rank
        COMMUNITY_CENTER.save()
    }

    fun getBitsAvailable(key: String): Long = COMMUNITY_CENTER.get().bitsAvailable[key] ?: 0

    fun setBitsAvailable(key: String, bits: Long) {
        COMMUNITY_CENTER.get().bitsAvailable[key] = bits
        COMMUNITY_CENTER.save()
    }
}
