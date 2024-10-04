package tech.thatgravyboat.skyblockapi.api.profile

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes

@Module
object ProfileAPI {

    //  SB Level: [15] 30/100 XP
    private val sbLevelRegex = Regexes.create(
        "tablist.profile.level",
        " SB Level: \\[(?<level>\\d+)] [\\d,]+/[\\d,]+ XP"
    )

    // Profile: Watermelon ♲
    private val profileRegex = Regexes.create(
        "tablist.profile.name",
        "Profile: (?<name>.+)"
    )

    var profileName: String? = null
        private set

    var profileType: ProfileType = ProfileType.UNKNOWN
        private set

    var sbLevel: Int = 0
        private set

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
        }

        sbLevelRegex.anyMatch(event.new, "level") { (level) ->
            sbLevel = level.toIntValue()
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
