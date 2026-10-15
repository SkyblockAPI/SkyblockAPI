package tech.thatgravyboat.skyblockapi.api.profile.profile

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.data.stored.ProfileStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyNonGuest
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyWidget
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabListChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.DebugBuilder
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileLevelChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.utils.ApiDebug
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
import tech.thatgravyboat.skyblockapi.utils.extentions.since
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.regex.component.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.component.toComponentRegex
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import java.util.UUID
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

@Module
public object ProfileAPI {

    private val tablistGroup = RegexGroup.TABLIST.group("profile")

    private val coopRegex = tablistGroup.create(
        "coop",
        "\\s+ Coop \\((?<amount>\\d+)\\)",
    )

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
        "^Profile ID: (?<id>\\S+)",
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

    private var lastWorldSwap = Instant.DISTANT_PAST

    public var profileName: String? = null
        private set

    public var profileUuid: UUID?
        get() = ProfileStorage.profileId
        private set(value) {
            ProfileStorage.profileId = value
        }

    public val profileId: UUID? get() = profileUuid

    public var isLoaded: Boolean = false
        private set

    public val profileType: ProfileType get() = ProfileStorage.getProfileType()

    public val sbLevel: Int get() = ProfileStorage.getSkyBlockLevel()

    public val sbLevelProgress: Int get() = ProfileStorage.getSkyBlockLevelProgress()

    public val coop: Boolean get() = ProfileStorage.isCoop()

    public val bingoRank: SkyBlockRarity? get() = ProfileStorage.getBingoRank()


    public fun getLevelColor(): Int = getLevelColor(sbLevel)

    public fun getLevelColor(level: Int): Int = levelColors.entries.firstOrNull { level in it.key }?.value ?: TextColor.BLACK


    @Subscription
    internal fun onServerChange(event: ServerChangeEvent) {
        this.isLoaded = false
        this.lastWorldSwap = currentInstant()
    }

    @OnlyWidget(TabWidget.PROFILE)
    @Subscription(priority = Int.MIN_VALUE)
    internal fun onTabListWidgetChange(event: TabWidgetChangeEvent) {
        profileRegex.anyMatch(event.new, "name") { (name) ->
            val oldName = this.profileName
            val profileType = ProfileType.fromIcon(name.last())

            var profileName = name.trim(' ')
            if (profileType.icon != null) profileName = profileName.trim(profileType.icon)

            if (SkyBlockIsland.THE_RIFT.inIsland()) {
                profileName = profileName.reversed()
            }
            val first = profileName.firstOrNull()
            val last = profileName.lastOrNull()

            // we reverse the profile name if we detect that its reversed, as a fallback in case the profile name is
            // reversed and we are not in the rift
            if (first?.isLowerCase() == true && last?.isUpperCase() == true) {
                profileName = profileName.reversed()
            }

            this.isLoaded = true
            this.profileName = profileName
            // we need to call setProfileType AFTER doing all the transformations to profileName and after setting it
            // to the variable in the object
            ProfileStorage.setProfileType(profileType)

            if (oldName == profileName) return@anyMatch
            ProfileChangeEvent(profileName).post()
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

    @Subscription
    @OnlyIn(SkyBlockIsland.PRIVATE_ISLAND)
    @OnlyNonGuest
    internal fun onTablistUpdate(event: TabListChangeEvent) {
        val line = event.new.firstOrNull()?.firstOrNull()?.stripped ?: return
        coopRegex.match(line) {
            ProfileStorage.setCoop(true)
        }
    }

    @Subscription(priority = Int.MIN_VALUE, receiveCancelled = true)
    internal fun onChatMessage(event: ChatReceivedEvent.Pre) {
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
    internal fun onTick(event: TickEvent) {
        if (lastWorldSwap.since() > 2.5.seconds && !this.isLoaded && !LocationAPI.forceOnSkyblock) {
            SkyBlockAPI.logger.error("Could not find way to determine profile name.")
        }
    }

    @Subscription
    internal fun onProfileLevelChange(event: ProfileLevelChangeEvent) {
        ProfileStorage.setSkyBlockLevel(event.level)
    }

    @ApiDebug("Profile")
    internal fun debug(builder: DebugBuilder) = with(builder) {
        field("Name", profileName)
        field("Id", profileUuid)
        field("Type", profileType)
        field("Level", sbLevel)
        field("Is Coop", coop)
    }
}

public enum class ProfileType(internal val icon: Char? = null) {
    NORMAL,
    BINGO('Ⓑ'),
    IRONMAN('♲'),
    STRANDED('☀'),
    UNKNOWN,
    ;

    private val string = toFormattedName()

    override fun toString(): String = string

    internal companion object {
        internal fun fromIcon(char: Char) = entries.find { it.icon == char } ?: NORMAL
    }
}
