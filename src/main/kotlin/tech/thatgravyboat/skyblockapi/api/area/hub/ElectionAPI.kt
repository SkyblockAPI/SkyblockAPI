package tech.thatgravyboat.skyblockapi.api.area.hub

import me.owdding.ktmodules.Module
import net.minecraft.network.chat.Component
import net.minecraft.util.TriState
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.data.*
import tech.thatgravyboat.skyblockapi.api.data.stored.ElectionStorage
import tech.thatgravyboat.skyblockapi.api.data.stored.PERKPOCALYPSE_CANDIDATE_DURATION
import tech.thatgravyboat.skyblockapi.api.data.stored.StoredMayor
import tech.thatgravyboat.skyblockapi.api.datetime.SkyBlockInstant
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.InventoryTitle
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.MustBeContainer
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.info.MayorChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.DebugBuilder
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.ApiDebug
import tech.thatgravyboat.skyblockapi.utils.Scheduling
import tech.thatgravyboat.skyblockapi.utils.command.EnumArgument
import tech.thatgravyboat.skyblockapi.utils.command.MapBackedArgumentType
import tech.thatgravyboat.skyblockapi.utils.debugToggle
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
import tech.thatgravyboat.skyblockapi.utils.extentions.filterValuesNotNull
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.isInFuture
import tech.thatgravyboat.skyblockapi.utils.extentions.isInPast
import tech.thatgravyboat.skyblockapi.utils.extentions.since
import tech.thatgravyboat.skyblockapi.utils.extentions.sublistAfter
import tech.thatgravyboat.skyblockapi.utils.extentions.until
import tech.thatgravyboat.skyblockapi.utils.http.Http
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
import java.util.concurrent.ScheduledFuture
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

private const val URL = "https://api.hypixel.net/v2/resources/skyblock/election"
private const val MAYOR_SLOT = 37

private const val ELECTION_MONTH = 3 // Late spring
private const val ELECTION_DAY = 27

@Module
object ElectionAPI {

    private val disableElectionStorage by debugToggle("disable_election_storage", "Disables loading the Election Storage when starting the game.")

    private val chatGroup = RegexGroup.CHAT.group("election")
    private val electionOverRegex = chatGroup.create(
        "electionOver",
        "The election room is now closed\\. Clerk Seraphine is doing a final count of the votes\\.\\.\\.",
    )

    private var lastEvaluatedExtraJerry: Instant = currentInstant()
    private var scheduler: ScheduledFuture<*>? = null
    var rawData: ElectionJson? = null
        private set

    var mayor: MayorCandidate? = null
        private set
    var minister: MayorCandidate? = null
        private set
    var nextElection: Instant? = null
        private set

    var currentJerryCandidate: Pair<MayorCandidate, Instant>? = null
        private set

    // The keys are the numbers from 0-5, specifying which "index" in the rotation the mayors are
    val jerryPerkpocalypseRotation: Map<Int, MayorCandidate>
        get() = ElectionStorage.jerryPerkpocalypseRotation.mapValues { it.value.getCandidate() }.filterValuesNotNull()

    // What index of the jerry perkpocalypse rotation a certain instant is.
    fun getPerkpocalypseIndex(instant: Instant): Int? = ElectionStorage.indexOfPerkpocalypse(instant)

    val perkpocalypseRotationDuration: Duration
        get() = PERKPOCALYPSE_CANDIDATE_DURATION


    init {
        val loadedDataFromStorage = loadStoredData()
        if (!loadedDataFromStorage) {
            SkyBlockAPI.info("Couldn't load Election data from storage, requesting data instead.")
            updateScheduler(10.minutes)
        } else {
            SkyBlockAPI.info("Successfully loaded Election data from storage!")
            updateScheduler(10.minutes, initialDelay = 10.minutes)
        }
    }

    /**
     * Tries to load the current election data from storage.
     * Returns `false` if it needs to request the data
     * */
    private fun loadStoredData(): Boolean {
        if (disableElectionStorage) {
            SkyBlockAPI.info("Ignoring Election data from storage due to debug properties.")
            return false
        }
        for ((id, perk) in MayorPerks.perksMap) {
            val description = ElectionStorage.getPerkDescription(id) ?: continue
            perk.description = description
        }

        val mayorData = ElectionStorage.storedMayor
        val ministerData = ElectionStorage.storedMinister
        val nextTime = ElectionStorage.nextMayorTime

        if (nextTime.isInPast()) {
            ElectionStorage.resetElection()
            return false
        }

        fun setCandidate(data: StoredMayor?): MayorCandidate? {
            val candidate = data?.getCandidate() ?: return null
            data.perks.mapNotNull(MayorPerks::getPerkById)
                .forEach { perk ->
                    perk.active = true
                    candidate.perks.add(perk)
                }
            return candidate
        }

        this.mayor = setCandidate(mayorData)
        this.minister = setCandidate(ministerData)
        this.nextElection = nextTime

        val mayor = this.mayor
        // Special mayors cant have ministers
        if (mayor == null || (!mayor.isSpecial && this.minister == null)) {
            ElectionStorage.resetElection()
            resetData()
            return false
        }

        if (MayorPerks.PERKPOCALYPSE.active) {
            val perkpocalypseCandidate = ElectionStorage.getCurrentPerkpocalypse()?.getCandidate()
            val nextPerkpocalypseTime = ElectionStorage.nextPerkpocalypse()
            if (perkpocalypseCandidate != null && nextPerkpocalypseTime != null) {
                this.currentJerryCandidate = perkpocalypseCandidate.addAllPerks(includeNonPerkpocalypse = false) to nextPerkpocalypseTime
            }
        }
        MayorChangeEvent(mayor, this.minister).post()
        return true
    }

    private fun updateScheduler(
        time: Duration,
        updateSchedulerTo: Duration? = null,
        initialDelay: Duration = 0.seconds
    ) {
        scheduler?.cancel(false)
        scheduler = Scheduling.schedule(initialDelay, time) {
            check(updateSchedulerTo)
        }
    }

    @JvmStatic
    private suspend fun check(newSchedulerTime: Duration? = null) {
        SkyBlockAPI.info("Requesting Election Data from Hypixel API")
        val result = Http.getResult(URL, SkyblockAPICodecs.getCodec<ElectionJson>())
        val response = result.getOrNull() ?: run {
            SkyBlockAPI.error("Failed to get election data", result.exceptionOrNull())
            return
        }

        McClient.runNextTick {
            if (handleResponse(response)) {
                mayor?.let {
                    MayorChangeEvent(it, minister).post()
                    SkyBlockAPI.info("Found Mayor $it and Minister $minister")
                }

                if (newSchedulerTime != null) {
                    updateScheduler(newSchedulerTime)
                }
            }
        }
    }

    private fun handleResponse(response: ElectionJson?): Boolean {
        rawData = response
        val mayor = response?.mayor ?: return false

        val newMayor = MayorCandidates.register(mayor.name)
        if (newMayor == this.mayor) return false

        this.mayor = newMayor
        val storedMayor = StoredMayor.of(newMayor)

        val newMinister = mayor.minister?.name?.let(MayorCandidates::register)
        this.minister = newMinister


        MayorPerks.reset()
        mayor.perks.forEach {
            val perk = handlePerk(newMayor, it)
            storedMayor.perks.add(perk.id)
        }
        ElectionStorage.storedMayor = storedMayor

        if (newMinister != null && mayor.minister.perk != null) {
            val storedMinister = StoredMayor.of(newMinister)
            val perk = handlePerk(newMinister, mayor.minister.perk)
            storedMinister.perks.add(perk.id)
            ElectionStorage.storedMinister = storedMinister
        } else {
            ElectionStorage.storedMinister = null
        }

        val nextElection = calculateNextElection()
        this.nextElection = nextElection
        ElectionStorage.nextMayorTime = nextElection

        return true
    }

    private fun handlePerk(candidate: MayorCandidate, perk: PerkJson): MayorPerk {
        val perkData = MayorPerks.register(perk.name)
        perkData.active = true
        perkData.description = perk.description
        ElectionStorage.setPerkDescription(perkData.id, perk.description)
        candidate.perks.add(perkData)
        return perkData
    }

    @TimePassed("1s")
    @Subscription(TickEvent::class)
    fun onTick() {
        if (!MayorPerks.PERKPOCALYPSE.active) return
        val jerryInfo = currentJerryCandidate
        if (jerryInfo != null) {
            val (extraMayor, expireTime) = jerryInfo
            if (expireTime.isInFuture()) return
            extraMayor.clearAllPerks()
            currentJerryCandidate = null
        }

        val currentPerkpocalypse = ElectionStorage.getCurrentPerkpocalypse()?.getCandidate() ?: return
        val expireTime = ElectionStorage.nextPerkpocalypse() ?: return

        currentPerkpocalypse.addAllPerks(includeNonPerkpocalypse = false)
        currentJerryCandidate = currentPerkpocalypse to expireTime
        SkyBlockAPI.info("Jerry Mayor Changed Automatically: $currentPerkpocalypse, expires at $expireTime - in ${expireTime.until()}")
    }

    @Subscription
    @InventoryTitle("Calendar and Events")
    @MustBeContainer
    private fun ContainerInitializedEvent.onInventory() {
        if (lastEvaluatedExtraJerry.since() < 10.seconds) return
        lastEvaluatedExtraJerry = currentInstant()
        if (!MayorPerks.PERKPOCALYPSE.active) return
        val stack = itemStacks.getOrNull(MAYOR_SLOT).takeIf { it?.cleanName == "Mayor Jerry" } ?: return
        // TODO: add perk description for the perkpocalypse perk
        val foundPerk = stack.getRawLore().sublistAfter { it == "Perkpocalypse Perks:" }.firstNotNullOfOrNull { perk -> MayorPerks.getPerk(perk) } ?: return
        val extraMayor = MayorCandidates.mayors.find { foundPerk in it.perks } ?: return

        val expireTime = ElectionStorage.nextPerkpocalypse() ?: return

        currentJerryCandidate?.first?.clearAllPerks()

        val newCandidate = extraMayor.addAllPerks(includeNonPerkpocalypse = false) to expireTime
        val storedMayor = StoredMayor.of(extraMayor)
        ElectionStorage.setCurrentPerkpocalypse(storedMayor)

        currentJerryCandidate = newCandidate
        SkyBlockAPI.info("Jerry Mayor Detected: $extraMayor, expires at $expireTime - in ${expireTime.until()}")
    }

    @Subscription
    fun onChat(event: ChatReceivedEvent.Pre) {
        if (electionOverRegex.matches(event.text)) {
            // When the Election is over, schedule a check every minute until a new mayor is found, then schedule every 20 minutes
            updateScheduler(1.minutes, 20.minutes)
            resetData()
        }
    }

    private fun calculateNextElection(): Instant {
        return SkyBlockInstant(year = SkyBlockInstant.now().year + 1, month = ELECTION_MONTH, day = ELECTION_DAY).instant
    }

    private fun resetData() {
        mayor = null
        minister = null
        currentJerryCandidate = null
        nextElection = Instant.DISTANT_PAST
        ElectionStorage.resetElection()
        MayorPerks.reset()
    }

    @Subscription
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        event.register("sbapi election") {
            then("storage", "cache") {
                then("reset") {
                    callback {
                        ElectionStorage.resetElection()
                        resetData()
                        updateScheduler(1.minutes, 20.minutes, initialDelay = 1.minutes)
                        Text.sendDebug("Reset the Election Cache Storage!")
                    }
                    thenCallback("perkpocalypse") {
                        ElectionStorage.clearPerkpocalypse()
                        currentJerryCandidate = null
                        Text.sendDebug("Reset the Perkpocalypse rotation!")
                    }
                    thenCallback("descriptions") {
                        ElectionStorage.resetDescriptions()
                        Text.sendDebug("Reset descriptions in the Election Cache Storage!")
                    }
                }
            }
            then("perk override") {
                then("perk", MapBackedArgumentType(MayorPerks.perksMap)) {
                    callback {
                        val perk = argument<MayorPerk>("perk")
                        Text.sendDebug("State of perk ") {
                            append(perk.id) {
                                color = TextColor.AQUA
                                hover = Text.of(perk.perkName, TextColor.GOLD)
                            }
                            append(" is ")
                            append(perk.overrideState.name, TextColor.GOLD)
                            append(".")
                        }
                    }
                    thenCallback("state", EnumArgument<TriState>()) {
                        val perk = argument<MayorPerk>("perk")
                        val state = argument<TriState>("state")
                        perk.overrideState = state
                        Text.sendDebug("Changed state of perk ") {
                            append(perk.id) {
                                color = TextColor.AQUA
                                hover = Text.of(perk.perkName, TextColor.GOLD)
                            }
                            append(" to ")
                            append(state.name, TextColor.GOLD)
                            append(".")
                        }
                    }
                }
            }
        }
    }


    @ApiDebug("ElectionAPI", "election copy")
    internal fun debug(builder: DebugBuilder) = with(builder) {
        fun info(name: String, value: Any): Component = Text.of("$name: ").append(format(value))
        fun Collection<MayorPerk>.asString() = joinToString(transform = MayorPerk::id)
        fun MayorCandidate?.hoverComponent(): Component? {
            if (this == null) return null
            return Text.join(
                info("id", id),
                info("candidateName", candidateName),
                info("perks", perks.asString()),
                info("isSpecial", isSpecial),
                separator = CommonText.NEWLINE
            )
        }

        field(::mayor, description = mayor.hoverComponent())
        field(::minister, description = minister.hoverComponent())
        field(::nextElection)

        currentJerryCandidate?.let { (jerryCandidate, jerryCandidateExpireTime) ->
            field("jerryCandidate", jerryCandidate, description = jerryCandidate.hoverComponent())
            field("jerryCandidateExpireTime", jerryCandidateExpireTime)
        } ?: field("jerryCandidate", null)

        field("activePerks", MayorPerks.perks.filter(MayorPerk::active).asString())
        field(MayorPerks::foxyExtraEventType)

        field("overridenPerks", MayorPerks.perks.filter { it.overrideState != DEFAULT }.asString())

        field("rawData", "Click to copy".takeUnless { rawData == null }, copyValue = rawData?.toString())
    }

}
