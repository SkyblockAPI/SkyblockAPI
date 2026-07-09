package tech.thatgravyboat.skyblockapi.api.data.stored

import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.OptionalIfEmpty
import me.owdding.ktcodecs.OptionalNullable
import tech.thatgravyboat.skyblockapi.api.data.StoredData
import kotlin.time.Instant

internal object ElectionStorage {

    private val DATA = StoredData<ElectionData>("election.json")

    var mayor: StoredMayor?
        get() = DATA.get().mayor
        set(value) = DATA.edit {
            if (this.mayor == value) return
            this.mayor = value
        }

    var minister: StoredMayor?
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

    val jerryPerkpocalypseRotation: List<StoredMayor?>
        get() = DATA.get().jerryPerkpocalypseRotation

    fun getPerkpocalypse(index: Int): StoredMayor? = DATA.get().jerryPerkpocalypseRotation.getOrNull(index)

    fun setPerkpocalypse(index: Int, mayor: StoredMayor) {
        DATA.edit {
            if (jerryPerkpocalypseRotation.getOrNull(index) == mayor) return
            jerryPerkpocalypseRotation[index] = mayor
        }
    }

    fun clearPerkpocalypse() = DATA.get().jerryPerkpocalypseRotation.clear()

    fun getPerkDescription(perkId: String): String? = DATA.get().perkDescriptions[perkId]

    /** Returns `true` if it changed the description */
    fun setPerkDescription(perkId: String, description: String): Boolean {
        DATA.edit {
            if (perkDescriptions[perkId] == description) return false
            perkDescriptions[perkId] = description
            return true
        }
    }

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
    @OptionalNullable var mayor: StoredMayor? = null,
    @OptionalNullable var minister: StoredMayor? = null,
    var nextMayorTime: Instant = Instant.DISTANT_PAST,
    @OptionalIfEmpty val jerryPerkpocalypseRotation: MutableList<StoredMayor?> = mutableListOf(),

    @OptionalIfEmpty val perkDescriptions: MutableMap<String, String> = mutableMapOf(),
)

@GenerateCodec
internal data class StoredMayor(
    val id: String,
    val perks: MutableList<String> = mutableListOf(),
)
