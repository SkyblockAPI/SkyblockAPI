package tech.thatgravyboat.skyblockapi.api.area.hub

import me.owdding.ktmodules.Module
import net.minecraft.util.TriState
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.data.*
import tech.thatgravyboat.skyblockapi.api.datetime.SkyBlockInstant
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.InventoryTitle
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.MustBeContainer
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.info.MayorChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.MayorUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.Scheduling
import tech.thatgravyboat.skyblockapi.utils.command.EnumArgument
import tech.thatgravyboat.skyblockapi.utils.command.MapBackedArgumentType
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.sublistAfter
import tech.thatgravyboat.skyblockapi.utils.extentions.until
import tech.thatgravyboat.skyblockapi.utils.http.Http
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
import tech.thatgravyboat.skyblockapi.utils.time.currentInstant
import tech.thatgravyboat.skyblockapi.utils.time.since
import java.util.concurrent.ScheduledFuture
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

private const val URL = "https://api.hypixel.net/v2/resources/skyblock/election"
private const val MAYOR_SLOT = 37

@Module
object ElectionAPI {

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
    var currentJerryCandidate: Pair<MayorCandidate, Instant>? = null
        private set

    @RemoveNextVersion(ReplaceWith("mayor"))
    val currentMayor: Candidate?
        get() = mayor?.let(Candidate::fromMayorCandidate)

    @RemoveNextVersion(ReplaceWith("minister"))
    val currentMinister: Candidate?
        get() = minister?.let(Candidate::fromMayorCandidate)

    @RemoveNextVersion(ReplaceWith("currentJerryCandidate"))
    val jerryCandidate: Pair<Candidate, Instant>?
        get() = currentJerryCandidate?.let {
            Candidate.fromMayorCandidate(it.first) to it.second
        }


    init {
        updateScheduler(10.minutes)
    }

    private fun updateScheduler(time: Duration, updateSchedulerTo: Duration? = null) {
        scheduler?.cancel(false)
        scheduler = Scheduling.schedule(0.seconds, time) {
            check(updateSchedulerTo)
        }
    }

    @JvmStatic
    private suspend fun check(newSchedulerTime: Duration? = null) {
        val result = Http.getResult<ElectionJson>(URL)
        val response = result.getOrNull() ?: return

        McClient.runNextTick {
            if (handleResponse(response)) {
                mayor?.let { MayorChangeEvent(it, minister).post() }
                currentMayor?.let { MayorUpdateEvent(it, currentMinister).post() }

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
        val newMinister = mayor.minister?.name?.let(MayorCandidates::register)
        this.minister = newMinister

        MayorPerks.reset()
        mayor.perks.forEach { handlePerk(newMayor, it) }
        if (newMinister != null) {
            handlePerk(newMinister, mayor.minister.perk)
        }

        return true
    }

    private fun handlePerk(candidate: MayorCandidate, perk: PerkJson) {
        val perkData = MayorPerks.register(perk.name)
        perkData.active = true
        perkData.description = perk.description
        candidate.perks.add(perkData)
    }

    @Subscription
    @InventoryTitle("Calendar and Events")
    @MustBeContainer
    private fun ContainerInitializedEvent.onInventory() {
        if (lastEvaluatedExtraJerry.since() < 10.seconds) return
        lastEvaluatedExtraJerry = currentInstant()
        if (!MayorPerks.PERKPOCALYPSE.active) return
        val electionYear = rawData?.mayor?.election?.year ?: return
        val stack = itemStacks.getOrNull(MAYOR_SLOT).takeIf { it?.cleanName == "Mayor Jerry" } ?: return
        // TODO: add perk description for the perkpocalypse perk
        val foundPerk = stack.getRawLore().sublistAfter { it == "Perkpocalypse Perks:" }.firstNotNullOfOrNull { perk -> MayorPerks.getPerk(perk) }
        val extraMayor = MayorCandidates.mayors.find { foundPerk in it.perks } ?: return

        val nextElection = SkyBlockInstant(electionYear + 2, 3, 27).instant // Late Spring 27th, 2 years after the election opened

        val expireTime = (1..21).map { nextElection - (6.hours * it) }.lastOrNull { it > currentInstant() }?.coerceAtMost(nextElection) ?: return

        currentJerryCandidate?.first?.clearAllPerks()

        currentJerryCandidate = extraMayor.addAllPerks() to expireTime
        SkyBlockAPI.info("Jerry Mayor Detected: $extraMayor, expires at $expireTime")
    }

    @Subscription
    fun onChat(event: ChatReceivedEvent.Pre) {
        if (electionOverRegex.matches(event.text)) {
            // When the Election is over, schedule a check every minute until a new mayor is found, then schedule every 20 minutes
            updateScheduler(1.minutes, 20.minutes)
        }
    }

    @Subscription
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        event.register("sbapi election") {
            then("perk override") {
                then("perk", MapBackedArgumentType(MayorPerks.perksMap)) {
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
            thenCallback("copy") {
                McClient.clipboard = buildString {
                    appendLine("Current mayor: $mayor")
                    appendLine("Current minister: $minister")
                    currentJerryCandidate?.let { (candidate, time) ->
                        appendLine("Current Jerry Candidate: $candidate [${time.until()}]")
                    }
                    appendLine("Active perks: ${MayorPerks.perks.filter { it.active }}")
                    val overridden = MayorPerks.perks.filter { it.overrideState != DEFAULT }
                    if (overridden.isNotEmpty()) {
                        appendLine("Perks with override: ")
                        overridden.forEach { perk ->
                            appendLine("  - ${perk.id}: ${perk.overrideState}")
                        }
                    }

                    appendLine("RawData: $rawData")
                }
                Text.sendDebug("Copied Election Data to clipboard!")
            }
        }
    }

}
