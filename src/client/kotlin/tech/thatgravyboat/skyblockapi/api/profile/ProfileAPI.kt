package tech.thatgravyboat.skyblockapi.api.profile

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileLevelChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyblockIsland
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match

@Module
object ProfileAPI {

    private val profileGroup = RegexGroup.TABLIST_WIDGET.group("profile")

    // Profile: Watermelon ♲
    private val profileRegex = profileGroup.create(
        "name",
        "Profile: (?<name>.+)",
    )

    private val chatGroup = RegexGroup.CHAT.group("profile")

    private val coopProfileJoinRegex = chatGroup.create(
        "coopProfileJoin",
        "You are playing on profile: (?<profile>.*) \\(Co-op\\)",
    )

    // TODO: Store on disk
    private var coopProfiles = mutableSetOf<String>()


    var profileName: String? = null
        private set

    var profileType: ProfileType = ProfileType.UNKNOWN
        private set

    var sbLevel: Int = 0
        private set

    var coop: Boolean = false
        private set
        get() = coopProfiles.contains(profileName)

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
                    this.profileType = ProfileType.STRANDDED
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
    fun onChat(event: ChatReceivedEvent) {
        coopProfileJoinRegex.match(event.text, "profile") { (profile) ->
            coopProfiles.add(profile)
        }
    }
}

enum class ProfileType {
    NORMAL,
    BINGO,
    IRONMAN,
    STRANDDED,
    UNKNOWN
}
