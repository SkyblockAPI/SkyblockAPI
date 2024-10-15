package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredPlayerData
import tech.thatgravyboat.skyblockapi.api.profile.community.CommunityCenterData
import tech.thatgravyboat.skyblockapi.api.profile.community.FameRank
import tech.thatgravyboat.skyblockapi.api.profile.community.FameRanks
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileAPI

internal object CommunityCenterStorage {
    private val COMMUNITY_CENTER = StoredPlayerData(
        ::CommunityCenterData,
        CommunityCenterData.CODEC,
        "community_center.json",
    )

    private inline val data get() = COMMUNITY_CENTER.get()

    var rank: FameRank?
        get() = data.rank?.let { FameRanks.getByName(it) }
        set(value) {
            if (value == null || rank == value) return
            data.rank = value.name
            COMMUNITY_CENTER.save()
        }

    var bitsAvailable: Long
        get() {
            val profile = ProfileAPI.profileName ?: return 0L
            return data.bitsAvailable[profile] ?: 0
        }
        set(value) {
            if (value == bitsAvailable) return
            val profile = ProfileAPI.profileName ?: return
            data.bitsAvailable[profile] = value
            COMMUNITY_CENTER.save()
        }
}
