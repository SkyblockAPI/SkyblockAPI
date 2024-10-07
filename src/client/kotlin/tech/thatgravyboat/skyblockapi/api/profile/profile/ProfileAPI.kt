package tech.thatgravyboat.skyblockapi.api.profile.profile

import tech.thatgravyboat.skyblockapi.api.data.stored.ProfileStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardTitleUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileLevelChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyblockIsland
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch

@Module
object ProfileAPI {

    private val profileGroup = RegexGroup.TABLIST_WIDGET.group("profile")

    // Profile: Watermelon ♲
    private val profileRegex = profileGroup.create(
        "name",
        "Profile: (?<name>.+)",
    )


    var profileName: String? = null
        private set

    val profileType: ProfileType get() = ProfileStorage.getProfileType()

    val sbLevel: Int get() = ProfileStorage.getSkyblockLevel()

    val coop: Boolean get() = ProfileStorage.isCoop()

    @Subscription
    fun onTabListWidgetChange(event: TabWidgetChangeEvent) {
        if (event.widget != TabWidget.PROFILE) return
        profileRegex.anyMatch(event.new, "name") { (name) ->
            when {
                name.endsWith("♲") -> {
                    this.profileName = name.trim(' ', '♲')
                    ProfileStorage.setProfileType(ProfileType.IRONMAN)
                }

                name.endsWith("Ⓑ") -> {
                    this.profileName = name.trim(' ', 'Ⓑ')
                    ProfileStorage.setProfileType(ProfileType.BINGO)
                }

                name.endsWith("☀") -> {
                    this.profileName = name.trim(' ', '☀')
                    ProfileStorage.setProfileType(ProfileType.STRANDED)
                }

                else -> {
                    this.profileName = name
                    ProfileStorage.setProfileType(ProfileType.NORMAL)
                }
            }
            if (SkyblockIsland.THE_RIFT.inIsland()) {
                this.profileName = this.profileName?.reversed()
            }
        }
    }

    @Subscription
    fun onProfileLevelChange(event: ProfileLevelChangeEvent) {
        ProfileStorage.setSkyblockLevel(event.level)
    }

    @Subscription
    fun onScoreboardTitleUpdate(event: ScoreboardTitleUpdateEvent) {
        if (event.new.contains("CO-OP")) {
            ProfileStorage.setCoop(true)
        }
    }
}

enum class ProfileType {
    NORMAL,
    BINGO,
    IRONMAN,
    STRANDED,
    UNKNOWN
}
