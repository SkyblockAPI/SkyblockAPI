package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.data.StoredPlayerData
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileAPI
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileData
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileType
import tech.thatgravyboat.skyblockapi.utils.extentions.putIfDifferent
import java.util.UUID

internal object ProfileStorage {
    private val PROFILE = StoredPlayerData(
        { ProfileData(bingoRank = null) },
        ProfileData.CODEC,
        "profiles.json",
    )

    private inline val data get(): ProfileData = PROFILE.get()

    fun getProfileTypes(): Map<String, ProfileType> = data.profileType
    fun getProfileType(): ProfileType = data.profileType[ProfileAPI.profileName] ?: ProfileType.UNKNOWN

    /** Returns `true` if it changed anything */
    fun setProfileType(profileName: String, profileType: ProfileType): Boolean {
        if (SkyBlockIsland.inAnyIsland(SkyBlockIsland.THE_CATACOMBS, SkyBlockIsland.KUUDRA)) {
            // Don't allow changing profile type in dungeons or kuudra
            // The Tablist is different and doesn't contain the profile type
            // (At least Catacombs, I assume kuudra does the same)
            return false
        }

        return PROFILE.edit {
            this.profileType.putIfDifferent(profileName, profileType)
        }
    }

    fun getProfileId(): UUID? = data.profileId[ProfileAPI.profileName]

    /** Returns `true` if it changed anything */
    fun setProfileId(profileName: String, id: UUID): Boolean {
        return PROFILE.edit {
            this.profileId.putIfDifferent(profileName, id)
        }
    }

    fun getProfileById(id: UUID): String? {
        return data.profileId.entries.find { it.value == id }?.key
    }

    fun getSkyBlockLevel(): Int = data.sbLevel[ProfileAPI.profileName] ?: 0

    fun setSkyBlockLevel(profileName: String, level: Int) {
        PROFILE.edit {
            this.sbLevel.putIfDifferent(profileName, level)
        }
    }

    fun getSkyBlockLevelProgress(): Int = data.sbLevelProgress[ProfileAPI.profileName] ?: 0

    fun setSkyBlockLevelProgress(profileName: String, levelProgress: Int) {
        PROFILE.edit {
            this.sbLevelProgress.putIfDifferent(profileName, levelProgress)
        }
    }

    fun isCoop(): Boolean = data.coop[ProfileAPI.profileName] == true

    fun setCoop(profileName: String, isCoop: Boolean) {
        PROFILE.edit {
            this.coop.putIfDifferent(profileName, isCoop)
        }
    }

    fun getBingoRank(): SkyBlockRarity? = data.bingoRank

    fun setBingoRank(bingoRank: SkyBlockRarity?) {
        PROFILE.edit {
            if (this.bingoRank == bingoRank) return
            this.bingoRank = bingoRank
        }
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

}
