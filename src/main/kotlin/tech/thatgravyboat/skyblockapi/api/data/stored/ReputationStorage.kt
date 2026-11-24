package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.reputation.Faction
import tech.thatgravyboat.skyblockapi.api.profile.reputation.ReputationData

internal object ReputationStorage {

    private val REPUTATION = StoredProfileData(
        ::ReputationData,
        ReputationData.CODEC,
        "reputation.json",
    )

    var currentFaction: Faction?
        get() = REPUTATION.get()?.selectedFaction
        private set(value) {
            REPUTATION.get()?.selectedFaction = value
        }

    val reputation: MutableMap<Faction, Int>
        get() = REPUTATION.get()?.reputation ?: mutableMapOf()


    fun updateFaction(type: Faction?) {
        val prevFaction = currentFaction
        if (prevFaction == type) return
        currentFaction = type
        REPUTATION.save()
    }

    fun updateReputation(type: Faction, amount: Int) {
        val prevReputation = reputation[type]
        if (prevReputation == amount) return
        reputation[type] = amount
        REPUTATION.save()
    }
}
