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

    var profileType: ProfileType
        private set(value) {
            ProfileStorage.setProfileType(value)
        }
        get() = ProfileStorage.getProfileType()

    var sbLevel: Int
        private set(value) {
            ProfileStorage.setSkyblockLevel(value)
        }
        get() = ProfileStorage.getSkyblockLevel()

    var coop: Boolean
        private set(value) {
            ProfileStorage.setCoop(value)
        }
        get() = ProfileStorage.isCoop()

    @Subscription
    fun onTabListWidgetChange(event: TabWidgetChangeEvent) {
        if (event.widget != TabWidget.PROFILE) return
        profileRegex.anyMatch(event.new, "name") { (name) ->
            when {
                name.endsWith("♲") -> {
                    this.profileName = name.trim(' ', '♲')
                    this.profileType = ProfileType.IRONMAN
                }

                name.endsWith("Ⓑ") -> {
                    this.profileName = name.trim(' ', 'Ⓑ')
                    this.profileType = ProfileType.BINGO
                }

                name.endsWith("☀") -> {
                    this.profileName = name.trim(' ', '☀')
                    this.profileType = ProfileType.STRANDED
                }

                else -> {
                    this.profileName = name
                    this.profileType = ProfileType.NORMAL
                }
            }
            if (SkyblockIsland.THE_RIFT.inIsland()) {
                this.profileName = this.profileName?.reversed()
            }
        }
    }

    @Subscription
    fun onProfileLevelChange(event: ProfileLevelChangeEvent) {
        this.sbLevel = event.level
    }

    @Subscription
    fun onScoreboardTitleUpdate(event: ScoreboardTitleUpdateEvent) {
        println(event.new)
        if (event.new.contains("CO-OP")) {
            this.coop = true
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
