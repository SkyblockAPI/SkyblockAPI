package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredData
import tech.thatgravyboat.skyblockapi.api.profile.community.CommunityCenterData
import tech.thatgravyboat.skyblockapi.api.profile.community.FameRank
import tech.thatgravyboat.skyblockapi.api.profile.community.FameRanks
import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.codecs.CodecUtils
import java.util.*

internal object CommunityCenterStorage {
    private val COMMUNITY_CENTER = StoredData(
        mutableMapOf(),
        CodecUtils.map(
            KCodec.getCodec<UUID>(),
            CommunityCenterData.CODEC
        ),
        "community_center.json",
    )

    private val UUID.data get(): CommunityCenterData = COMMUNITY_CENTER.get().getOrPut(this, ::CommunityCenterData)

    fun getRank(uuid: UUID): FameRank? = uuid.data.rank?.let { FameRanks.getByName(it) }

    fun setRank(uuid: UUID, rank: FameRank?) {
        rank ?: return
        if (rank == getRank(uuid)) return
        uuid.data.rank = rank.name
        COMMUNITY_CENTER.save()
    }

    fun getBitsAvailable(profile: String): Long = McPlayer.uuid.data.bitsAvailable[profile] ?: 0

    fun setBitsAvailable(profile: String, bits: Long) {
        if (bits == getBitsAvailable(profile)) return
        McPlayer.uuid.data.bitsAvailable[profile] = bits
        COMMUNITY_CENTER.save()
    }
}
