package tech.thatgravyboat.skyblockapi.api.profile.profile

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.data.stored.ProfileStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyWidget
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiDebugEvent
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileLevelChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.regex.component.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.component.toComponentRegex
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import java.util.*

@Module
object ProfileAPI {

    private val widgetGroup = RegexGroup.TABLIST_WIDGET.group("profile")

    // Profile: Watermelon ♲
    private val profileRegex = widgetGroup.create(
        "name",
        "Profile: (?<name>.+)",
    )

    private val bingoRankRegex = widgetGroup.create(
        "bingo_rank",
        "Profile: .+ (?<type>Ⓑ)",
    ).toComponentRegex()

    private val skyBlockXPRegex = widgetGroup.create(
        "skyblockxp",
        "\\s*SB Level: \\[(?<level>\\d+)] (?<xp>\\d+).*",
    )

    private val chatGroup = RegexGroup.CHAT.group("profile")
    private val profileChatRegex = chatGroup.create(
        "name",
        "(You are playing on profile|Your profile was changed to): (?<name>\\S+)(?<coop> \\(Co-op\\))?",
    )

    private val profileIdRegex = chatGroup.create(
        "uuid",
        "^Profile ID: (?<id>\\S+)"
    )

    private val levelColors = mapOf(
        0..39 to TextColor.GRAY,
        40..79 to TextColor.WHITE,
        80..119 to TextColor.YELLOW,
        120..159 to TextColor.GREEN,
        160..199 to TextColor.DARK_GREEN,
        200..239 to TextColor.AQUA,
        240..279 to TextColor.DARK_AQUA,
        280..319 to TextColor.BLUE,
        320..359 to TextColor.LIGHT_PURPLE,
        360..399 to TextColor.DARK_PURPLE,
        400..439 to TextColor.GOLD,
        440..479 to TextColor.RED,
        480..Int.MAX_VALUE to TextColor.DARK_RED,
    )

    private var lastWorldSwap = 0L

    var profileName: String? = null
        private set

    var profileUuid: UUID?
        private set(value) {
            ProfileStorage.profileId = value
        }
        get() = ProfileStorage.profileId

    val profileId: UUID? get() = profileUuid

    var isLoaded: Boolean = false
        private set

    val profileType: ProfileType get() = ProfileStorage.getProfileType()

    val sbLevel: Int get() = ProfileStorage.getSkyBlockLevel()

    val sbLevelProgress: Int get() = ProfileStorage.getSkyBlockLevelProgress()

    val coop: Boolean get() = ProfileStorage.isCoop()

    val bingoRank: SkyBlockRarity? get() = ProfileStorage.getBingoRank()


    fun getLevelColor(): Int = getLevelColor(sbLevel)

    fun getLevelColor(level: Int): Int = levelColors.entries.firstOrNull { level in it.key }?.value ?: TextColor.BLACK


    @Subscription
    fun onServerChange(event: ServerChangeEvent) {
        this.isLoaded = false
        this.lastWorldSwap = System.currentTimeMillis()
    }

    @OnlyWidget(TabWidget.PROFILE)
    @Subscription(priority = Int.MIN_VALUE)
    fun onTabListWidgetChange(event: TabWidgetChangeEvent) {
        profileRegex.anyMatch(event.new, "name") { (name) ->
            val oldName = this.profileName
            when (name.last()) {
                '♲' -> {
                    this.profileName = name.trim(' ', '♲')
                    ProfileStorage.setProfileType(ProfileType.IRONMAN)
                }

                'Ⓑ' -> {
                    this.profileName = name.trim(' ', 'Ⓑ')
                    ProfileStorage.setProfileType(ProfileType.BINGO)
                }

                '☀' -> {
                    this.profileName = name.trim(' ', '☀')
                    ProfileStorage.setProfileType(ProfileType.STRANDED)
                }

                else -> {
                    this.profileName = name
                    ProfileStorage.setProfileType(ProfileType.NORMAL)
                }
            }
            if (SkyBlockIsland.THE_RIFT.inIsland()) {
                this.profileName = this.profileName?.reversed()
            }

            if (oldName != this.profileName) {
                ProfileChangeEvent(this.profileName!!).post()
            }
            this.isLoaded = true
        }

        skyBlockXPRegex.anyMatch(event.new, "level", "xp") { (level, progress) ->
            ProfileStorage.setSkyBlockLevelProgress(progress.toInt())
            ProfileStorage.setSkyBlockLevel(level.toInt())
        }

        if (profileType == ProfileType.BINGO) {
            bingoRankRegex.anyMatch(event.newComponents, "type") { (type) ->
                val bingoLevel = type.style.color?.let { SkyBlockRarity.fromColorOrNull(it.value) }
                ProfileStorage.setBingoRank(bingoLevel)
            }
        }
    }

    @Subscription(priority = Int.MIN_VALUE, receiveCancelled = true)
    fun onChatMessage(event: ChatReceivedEvent.Pre) {
        profileChatRegex.match(event.text) { groups ->
            val name = groups["name"] ?: return@match
            if (name != this.profileName) {
                this.profileName = name
                ProfileChangeEvent(this.profileName!!).post()
            }
            this.isLoaded = true
            ProfileStorage.setCoop(groups["coop"] != null)
        }
        profileIdRegex.match(event.text, "id") { (id) ->
            try {
                ProfileStorage.profileId = UUID.fromString(id)
            } catch (exception: IllegalStateException) {
                SkyBlockAPI.warn("(ProfileApi) Failed to parse profile id $id", exception)
            }
        }
    }

    @Subscription
    @OnlyOnSkyBlock
    @TimePassed("5s")
    fun onTick(event: TickEvent) {
        if (lastWorldSwap + 2500 < System.currentTimeMillis() && !this.isLoaded && !LocationAPI.forceOnSkyblock) {
            SkyBlockAPI.logger.error("Could not find way to determine profile name.")
        }
    }

    @Subscription
    fun onProfileLevelChange(event: ProfileLevelChangeEvent) {
        ProfileStorage.setSkyBlockLevel(event.level)
    }

    @Subscription
    private fun RegisterSkyblockApiDebugEvent.registerDebug() {
        register("Profile") {
            field("Name", profileName)
            field("Id", profileUuid)
            field("Type", profileType)
            field("Level", sbLevel)
            field("Is Coop", coop)
        }
    }
}

enum class ProfileType {
    NORMAL,
    BINGO,
    IRONMAN,
    STRANDED,
    UNKNOWN,
    ;

    private val string = toFormattedName()

    override fun toString(): String = string
}
