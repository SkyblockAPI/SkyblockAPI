package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredData
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileAPI
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileData
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileType

internal object ProfileStorage {
    private val PROFILE = StoredData(
        ProfileData(),
        ProfileData.CODEC,
        StoredData.defaultPath.resolve("profile.json"),
    )

    fun getProfileType(): ProfileType = PROFILE.get().profileType[ProfileAPI.profileName] ?: ProfileType.UNKNOWN

    fun setProfileType(profileType: ProfileType) {
        if (profileType == getProfileType()) return
        val profileName = ProfileAPI.profileName ?: return
        PROFILE.get().profileType[profileName] = profileType
        PROFILE.save()
    }

    fun getSkyblockLevel(): Int = PROFILE.get().sbLevel[ProfileAPI.profileName] ?: 0

    fun setSkyblockLevel(level: Int) {
        if (level == getSkyblockLevel()) return
        val profileName = ProfileAPI.profileName ?: return
        PROFILE.get().sbLevel[profileName] = level
        PROFILE.save()
    }

    fun isCoop(): Boolean = PROFILE.get().coop[ProfileAPI.profileName] == true

    fun setCoop(coop: Boolean) {
        if (coop == isCoop()) return
        val profileName = ProfileAPI.profileName ?: return
        PROFILE.get().coop[profileName] = coop
        PROFILE.save()
    }

}
