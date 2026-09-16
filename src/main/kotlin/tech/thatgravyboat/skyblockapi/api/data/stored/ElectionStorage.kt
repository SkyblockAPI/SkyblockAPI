package tech.thatgravyboat.skyblockapi.api.data.stored

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.IncludedCodec
import me.owdding.ktcodecs.NamedCodec
import me.owdding.ktcodecs.OptionalIfEmpty
import me.owdding.ktcodecs.OptionalNullable
import tech.thatgravyboat.skyblockapi.api.data.MayorCandidate
import tech.thatgravyboat.skyblockapi.api.data.MayorCandidates
import tech.thatgravyboat.skyblockapi.api.data.MayorPerk
import tech.thatgravyboat.skyblockapi.api.data.StoredData
import tech.thatgravyboat.skyblockapi.api.datetime.skyblockDays
import tech.thatgravyboat.skyblockapi.api.datetime.skyblockYears
import tech.thatgravyboat.skyblockapi.generated.CodecUtils
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import kotlin.math.floor
import kotlin.time.Instant

private val FULL_ELECTION_CYCLE = 1.skyblockYears
private const val PERKPOCALYPSE_CANDIDATES = 6
internal val PERKPOCALYPSE_CANDIDATE_DURATION = 18.skyblockDays

internal object ElectionStorage {

    private val DATA = StoredData<ElectionData>("election.json")

    var storedMayor: StoredMayor?
        get() = DATA.get().mayor
        set(value) = DATA.edit {
            if (this.mayor == value) return
            this.mayor = value
        }

    var storedMinister: StoredMayor?
        get() = DATA.get().minister
        set(value) = DATA.edit {
            if (this.minister == value) return
            this.minister = value
        }

    var nextMayorTime: Instant
        get() = DATA.get().nextMayorTime
        set(value) = DATA.edit {
            if (this.nextMayorTime == value) return
            this.nextMayorTime = value
        }

    val startMayorTime: Instant
        get() = nextMayorTime - FULL_ELECTION_CYCLE

    val jerryPerkpocalypseRotation: Map<Int, StoredMayor>
        get() = DATA.get().jerryPerkpocalypseRotation

    fun getPerkpocalypse(index: Int): StoredMayor? = DATA.get().jerryPerkpocalypseRotation[index]

    fun setPerkpocalypse(index: Int, mayor: StoredMayor) {
        DATA.edit {
            if (jerryPerkpocalypseRotation[index] == mayor) return
            jerryPerkpocalypseRotation[index] = mayor
        }
    }

    fun setCurrentPerkpocalypse(mayor: StoredMayor) {
        val index = getPerkpocalypseRealIndex(currentInstant()) ?: return
        setPerkpocalypse(index, mayor)
    }

    fun nextPerkpocalypse(instant: Instant = currentInstant()): Instant? {
        val index = getPerkpocalypseRealIndex(instant) ?: return null
        return startMayorTime + PERKPOCALYPSE_CANDIDATE_DURATION * index
    }

    private fun getPerkpocalypseRealIndex(instant: Instant): Int? {
        if (instant > nextMayorTime) return null
        return floor((instant - (nextMayorTime - FULL_ELECTION_CYCLE)) / PERKPOCALYPSE_CANDIDATE_DURATION).toInt()
    }

    fun indexOfPerkpocalypse(instant: Instant): Int? = getPerkpocalypseRealIndex(instant)?.rem(PERKPOCALYPSE_CANDIDATES)

    fun getCurrentPerkpocalypse(): StoredMayor? = indexOfPerkpocalypse(currentInstant())?.let(::getPerkpocalypse)

    fun clearPerkpocalypse() = DATA.get().jerryPerkpocalypseRotation.clear()

    //region Perk Descriptions
    fun getPerkDescription(perkId: String): String? = DATA.get().perkDescriptions[perkId]

    /** Returns `true` if it changed the description */
    fun setPerkDescription(perkId: String, description: String): Boolean {
        DATA.edit {
            if (perkDescriptions[perkId] == description) return false
            perkDescriptions[perkId] = description
            return true
        }
    }

    fun resetDescriptions() = DATA.edit { perkDescriptions.clear() }
    //endregion

    fun resetElection() {
        DATA.edit {
            mayor = null
            minister = null
            nextMayorTime = Instant.DISTANT_PAST
            jerryPerkpocalypseRotation.clear()
        }
    }
}


@GenerateCodec
internal data class ElectionData(
    @OptionalNullable
    var mayor: StoredMayor? = null,
    @OptionalNullable
    var minister: StoredMayor? = null,
    var nextMayorTime: Instant = Instant.DISTANT_PAST,
    @OptionalIfEmpty @NamedCodec("perkpocalypse_rotation")
    val jerryPerkpocalypseRotation: MutableMap<Int, StoredMayor> = mutableMapOf(),

    @OptionalIfEmpty
    val perkDescriptions: MutableMap<String, String> = mutableMapOf(),
) {
    companion object {
        // We need to use a custom codec because maps cannot use an integer as a key in JSON
        @IncludedCodec(named = "perkpocalypse_rotation")
        val PERKPOCALYPSE_ROTATION_CODEC: Codec<MutableMap<Int, StoredMayor>> = CodecUtils.map(
            Codec.STRING.xmap(Integer::parseInt, Int::toString),
            SkyblockAPICodecs.getCodec<StoredMayor>()
        ).orElseGet(::LinkedHashMap)
    }
}

@GenerateCodec
internal data class StoredMayor(
    val id: String,
    @OptionalIfEmpty val perks: MutableList<String> = mutableListOf(),
) {
    fun getCandidate(): MayorCandidate? = MayorCandidates.getCandidateById(id)
    companion object {
        fun of(candidate: MayorCandidate): StoredMayor {
            return StoredMayor(
                candidate.id,
                candidate.activePerks.mapTo(mutableListOf(), MayorPerk::id)
            )
        }
    }
}
