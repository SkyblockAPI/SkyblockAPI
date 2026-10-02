package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.data.StoredPlayerData
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileAPI
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileData
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileType
import java.util.UUID

internal object ProfileStorage {
    // Todo: use StoredProfileData instead, basically the same just better here
    private val PROFILE = StoredPlayerData(
        { ProfileData(bingoRank = null) },
        ProfileData.CODEC,
        "profiles.json",
    )

    private inline val data get(): ProfileData = PROFILE.get()

    fun getProfileTypes(): Map<String, ProfileType> = data.profileType
    fun getProfileType(): ProfileType = data.profileType[ProfileAPI.profileName] ?: ProfileType.UNKNOWN

    fun setProfileType(profileType: ProfileType) {
        if (SkyBlockIsland.inAnyIsland(SkyBlockIsland.THE_CATACOMBS, SkyBlockIsland.KUUDRA)) {
            // Don't allow changing profile type in dungeons or kuudra
            // The Tablist is different and doesn't contain the profile type
            // (At least Catacombs, I assume kuudra does the same)
            return
        }

        if (profileType == getProfileType()) return
        val profileName = ProfileAPI.profileName ?: return
        data.profileType[profileName] = profileType
        PROFILE.save()
    }

    fun getSkyBlockLevel(): Int = data.sbLevel[ProfileAPI.profileName] ?: 0

    fun setSkyBlockLevel(level: Int) {
        if (level == getSkyBlockLevel()) return
        val profileName = ProfileAPI.profileName ?: return
        data.sbLevel[profileName] = level
        PROFILE.save()
    }

    fun getSkyBlockLevelProgress(): Int = data.sbLevelProgress[ProfileAPI.profileName] ?: 0

    fun setSkyBlockLevelProgress(level: Int) {
        if (level == getSkyBlockLevelProgress()) return
        val profileName = ProfileAPI.profileName ?: return
        data.sbLevelProgress[profileName] = level
        PROFILE.save()
    }

    fun isCoop(): Boolean = data.coop[ProfileAPI.profileName] == true

    fun setCoop(coop: Boolean) {
        if (coop == isCoop()) return
        val profileName = ProfileAPI.profileName ?: return
        data.coop[profileName] = coop
        PROFILE.save()
    }

    fun getBingoRank(): SkyBlockRarity? = data.bingoRank

    fun setBingoRank(bingoRank: SkyBlockRarity?) {
        if (bingoRank == getBingoRank()) return
        data.bingoRank = bingoRank
        PROFILE.save()
    }

    fun getAllProfileNames(): Set<String> = buildSet {
        fun add(map: Map<String, Any>) = this.addAll(map.keys)
        with (data) {
            add(profileType)
            add(sbLevel)
            add(sbLevelProgress)
            add(coop)
            add(profileId)
        }
    }

    fun removeProfile(profileName: String) {
        fun MutableMap<String, out Any>.remove() = remove(profileName)
        data.apply {
            profileType.remove()
            sbLevel.remove()
            sbLevelProgress.remove()
            coop.remove()
            profileId.remove()
        }
        PROFILE.save()
    }

    var profileId: UUID?
        get() = data.profileId[ProfileAPI.profileName]
        set(value) {
            if (value == profileId || value == null) return
            val profileName = ProfileAPI.profileName ?: return
            data.profileId[profileName] = value
            PROFILE.save()
        }
}
