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
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.ServerDisconnectEvent
import tech.thatgravyboat.skyblockapi.api.events.location.SkyBlockLocationEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.DebugBuilder
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileLevelChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.remote.SkyBlockPvOpenedEvent
import tech.thatgravyboat.skyblockapi.api.events.remote.SkyBlockPvRequired
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.remote.PvLoadingHelper
import tech.thatgravyboat.skyblockapi.utils.ApiDebug
import tech.thatgravyboat.skyblockapi.utils.debugToggle
import tech.thatgravyboat.skyblockapi.utils.extentions.asString
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
import tech.thatgravyboat.skyblockapi.utils.extentions.enumSetOf
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.isNotEmpty
import tech.thatgravyboat.skyblockapi.utils.extentions.since
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.regex.component.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.component.toComponentRegex
import tech.thatgravyboat.skyblockapi.utils.regex.matchWhen
import tech.thatgravyboat.skyblockapi.utils.runCatchingWithPrint
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import java.util.UUID
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

@Module
public object ProfileAPI {

    //region Regex
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

    private val inventoryRegex = RegexGroup.INVENTORY.group("profile")
    internal val profileManagementTitle = inventoryRegex.create(
        "management.title",
        "Profile Management",
    )
    internal val profileManagementNameRegex = inventoryRegex.create(
        "management.name",
        """(?:. \S+ )?Profile: (?<profileName>.+)"""
    )
    private val profileManagementSelectedRegex = inventoryRegex.create(
        "management.selected",
        "You are playing on this profile!",
    )
    //endregion

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

    public val profileUuid: UUID?
        get() = ProfileStorage.getProfileId()

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

    private var profileLoadSource: ProfileLoadSource? = null

    private val useSingleProfile by debugToggle(
        "profile.use_single",
        "If no profile is detected in 10 seconds and we only have data of a single profile, we assume that's the profile you are playing on",
    )

    @Subscription(ServerDisconnectEvent::class, SkyBlockLocationEvent.Leave::class)
    private fun onDisconnect() {
        this.profileName = null
        this.isLoaded = false
        this.lastWorldSwap = currentInstant()
        this.profileLoadSource = null
    }

    @Subscription(ServerChangeEvent::class)
    internal fun onServerChange() {
        this.isLoaded = false
        this.lastWorldSwap = currentInstant()
    }

    @OnlyWidget(PROFILE)
    @Subscription(priority = Int.MIN_VALUE)
    internal fun onTabListWidgetChange(event: TabWidgetChangeEvent) {
        profileRegex.anyMatch(event.new, "name") { (name) ->
            val profileType = ProfileType.fromIcon(name.last())

            var profileName = name.trim(' ')
            if (profileType.icon != null) profileName = profileName.trim(profileType.icon, ' ')

            if (SkyBlockIsland.THE_RIFT.inIsland()) {
                profileName = profileName.reversed()
            }
            val first = profileName.firstOrNull()
            val last = profileName.lastOrNull()

            // we reverse the profile name if we detect that its reversed, as a fallback in case the
            // profile name is reversed, and we are not in the rift
            if (first?.isLowerCase() == true && last?.isUpperCase() == true) {
                profileName = profileName.reversed()
            }
            ProfileStorage.setProfileType(profileName, profileType)

            changeProfile(profileName, TABLIST)
        }

        skyBlockXPRegex.anyMatch(event.new, "level", "xp") { (level, progress) ->
            val profileName = this.profileName ?: return@anyMatch
            ProfileStorage.setSkyBlockLevelProgress(profileName, progress.toInt())
            ProfileStorage.setSkyBlockLevel(profileName, level.toInt())
        }

        if (profileType == ProfileType.BINGO) {
            bingoRankRegex.anyMatch(event.newComponents, "type") { (type) ->
                val bingoLevel = type.style.color?.let { SkyBlockRarity.fromColorOrNull(it.value) }
                ProfileStorage.setBingoRank(bingoLevel)
            }
        }
    }

    @OnlyNonGuest
    @OnlyIn(PRIVATE_ISLAND)
    @Subscription
    internal fun onTablistUpdate(event: TabListChangeEvent) {
        val line = event.new.firstOrNull()?.firstOrNull()?.stripped ?: return
        coopRegex.match(line) {
            val profileName = this.profileName ?: return@match
            ProfileStorage.setCoop(profileName, true)
        }
    }

    @Subscription(priority = Int.MIN_VALUE, receiveCancelled = true)
    internal fun onChatMessage(event: ChatReceivedEvent.Pre) {
        matchWhen(event.text) {
            case(profileChatRegex) { groups ->
                val profileName = groups["name"] ?: return@case
                ProfileStorage.setCoop(profileName, groups["coop"] != null)
                changeProfile(profileName, CHAT_NAME)
            }
            case(profileIdRegex, "id") { (id) ->
                val uuid = runCatching {
                    UUID.fromString(id)
                }.getOrElse {
                    SkyBlockAPI.warn("(ProfileAPI) Failed to parse profile id $id", it)
                    return@case
                }
                val profileName = profileName
                // if isLoaded is true, it means we already know the profile name
                if (isLoaded && profileName != null) {
                    ProfileStorage.setProfileId(profileName, uuid)
                    return@case
                }
                // fallback profile detection for when the `/profileid` command is sent
                val profileById = ProfileStorage.getProfileById(uuid)
                if (profileById == null) {
                    SkyBlockAPI.logger.error("Profile id '$id' didn't match any stored profiles")
                    return@case
                }
                changeProfile(profileById, CHAT_ID)
            }
        }
    }

    private fun changeProfile(newProfile: String, source: ProfileLoadSource) {
        if (!this.isLoaded) {
            // we only set this if it's not loaded, because that means
            // this is the first source since the server changed
            this.profileLoadSource = source
        }
        this.isLoaded = true

        if (this.profileName == newProfile) return
        // we also set the source if the profile name changes
        this.profileLoadSource = source
        this.profileName = newProfile
        ProfileChangeEvent(newProfile).post()
    }

    @OnlyOnSkyBlock
    @TimePassed("5s")
    @Subscription(TickEvent::class)
    internal fun onTick() {
        if (this.isLoaded) return

        // we ignore if force on skyblock, as this likely means we aren't on skyblock and don't have profile data during development
        if (LocationAPI.forceOnSkyblock) return

        // we use a higher warning delay if the last place we got the profile name from was the `/profileid` message or by having a single profile
        val warningDelay = if (this.profileLoadSource in ProfileLoadSource.extendedWarning) 10.seconds else 2.5.seconds
        if (this.lastWorldSwap.since() < warningDelay) return

        // if we only have a single profile, and we have useSingleProfile debug toggle enabled, we use that profile
        if (useSingleProfile && this.lastWorldSwap.since() > 10.seconds) {
            val single = ProfileStorage.getAllProfileNames().singleOrNull()
            if (single != null) {
                changeProfile(single, FORCED)
                return
            }
        }
        SkyBlockAPI.logger.error("Could not find way to determine profile name.")
    }

    @Subscription
    internal fun onProfileLevelChange(event: ProfileLevelChangeEvent) {
        val profileName = this.profileName ?: return
        ProfileStorage.setSkyBlockLevel(profileName, event.level)
    }

    // profile management inventory is used as a fallback in case we never know the profile name somehow
    @OnlyOnSkyBlock
    @Subscription
    private fun onProfileManagement(event: ContainerInitializedEvent) {
        if (!profileManagementTitle.matches(event.title)) return

        val currentProfileItem = event.containerItems.find { item ->
            item.isNotEmpty && profileManagementSelectedRegex.anyMatch(item.getRawLore())
        } ?: return

        val currentProfile = profileManagementNameRegex.findGroup(currentProfileItem.cleanName, "profileName") ?: return
        changeProfile(currentProfile, PROFILE_MANAGEMENT)
    }

    @OptIn(SkyBlockPvRequired::class)
    @Subscription
    internal fun onPvOpen(event: SkyBlockPvOpenedEvent) {
        val name = event.getPath("cute_name").asString() ?: return
        val profileType = ProfileType.fromApiName(event.getPath("game_mode").asString())

        val profileId = runCatchingWithPrint {
            UUID.fromString(event.getPath("profile_id").asString())
        }.getOrNull() ?: return

        val changedProfileType = ProfileStorage.setProfileType(name, profileType)
        val changedProfileId = ProfileStorage.setProfileId(name, profileId)

        if (changedProfileType || changedProfileId) PvLoadingHelper.markLoaded(PROFILE_DATA)
    }

    @ApiDebug("Profile")
    internal fun debug(builder: DebugBuilder) = with(builder) {
        field("Name", profileName)
        field("Id", profileUuid)
        field("Type", profileType)
        field("Level", sbLevel)
        field("Is Coop", coop)
        field("Load Source", profileLoadSource)
        field("Use Single Profile", useSingleProfile)
        field("Bingo Rank", bingoRank)
    }
}

private enum class ProfileLoadSource {
    TABLIST,
    CHAT_NAME,
    CHAT_ID,
    PROFILE_MANAGEMENT,
    FORCED,
    ;

    companion object {
        val extendedWarning = enumSetOf(CHAT_ID, FORCED)
    }
}

public enum class ProfileType(
    internal val icon: Char? = null,
    internal val apiName: String? = null,
) {
    NORMAL,
    BINGO('Ⓑ', apiName = "bingo"),
    IRONMAN('♲', apiName = "ironman"),
    STRANDED('☀', apiName = "island"),
    UNKNOWN,
    ;

    private val string = toFormattedName()

    override fun toString(): String = string

    public companion object {
        public fun fromApiName(name: String?): ProfileType {
            return if (name == null) NORMAL
            else entries.find { it.apiName == name } ?: NORMAL
        }

        internal fun fromIcon(char: Char) = entries.find { it.icon == char } ?: NORMAL
    }
}
