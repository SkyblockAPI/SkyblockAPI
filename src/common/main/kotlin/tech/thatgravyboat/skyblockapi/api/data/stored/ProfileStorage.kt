package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredPlayerData
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileAPI
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileData
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileType

internal object ProfileStorage {
    // Todo: use StoredProfileData instead, basically the same just better here
    private val PROFILE = StoredPlayerData(
        ::ProfileData,
        ProfileData.CODEC,
        "profiles.json",
    )

    private inline val data get(): ProfileData = PROFILE.get()

    fun getProfileType(): ProfileType = data.profileType[ProfileAPI.profileName] ?: ProfileType.UNKNOWN

    fun setProfileType(profileType: ProfileType) {
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

}
