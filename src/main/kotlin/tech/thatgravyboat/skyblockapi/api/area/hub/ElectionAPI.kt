package tech.thatgravyboat.skyblockapi.api.area.hub

import me.owdding.ktmodules.Module
import net.minecraft.util.TriState
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.data.*
import tech.thatgravyboat.skyblockapi.api.datetime.SkyBlockInstant
import tech.thatgravyboat.skyblockapi.api.datetime.skyblockYears
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.InventoryTitle
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.MustBeContainer
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.info.MayorChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.Scheduling
import tech.thatgravyboat.skyblockapi.utils.command.EnumArgument
import tech.thatgravyboat.skyblockapi.utils.command.MapBackedArgumentType
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.isInFuture
import tech.thatgravyboat.skyblockapi.utils.extentions.since
import tech.thatgravyboat.skyblockapi.utils.extentions.sublistAfter
import tech.thatgravyboat.skyblockapi.utils.extentions.until
import tech.thatgravyboat.skyblockapi.utils.http.Http
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
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
        val newMinister = mayor.minister?.name?.let(MayorCandidates::register)
        this.minister = newMinister

        MayorPerks.reset()
        mayor.perks.forEach { handlePerk(newMayor, it) }
        if (newMinister != null && mayor.minister.perk != null) {
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
        val electionYear = rawData?.mayor?.election?.year?.plus(1) ?: return
        val stack = itemStacks.getOrNull(MAYOR_SLOT).takeIf { it?.cleanName == "Mayor Jerry" } ?: return
        // TODO: add perk description for the perkpocalypse perk
        val foundPerk = stack.getRawLore().sublistAfter { it == "Perkpocalypse Perks:" }.firstNotNullOfOrNull { perk -> MayorPerks.getPerk(perk) } ?: return
        val extraMayor = MayorCandidates.mayors.find { foundPerk in it.perks } ?: return

        val termStart = SkyBlockInstant(electionYear, 3, 27).instant // Late Spring 27th

        val expireTime = (1..21).map { termStart + (6.hours * it) }.firstOrNull { it.isInFuture() }?.coerceAtMost(termStart + 1.skyblockYears) ?: return

        currentJerryCandidate?.first?.clearAllPerks()

        currentJerryCandidate = extraMayor.addAllPerks(includeNonPerkapocalypse = false) to expireTime
        SkyBlockAPI.info("Jerry Mayor Detected: $extraMayor, expires at $expireTime - in ${expireTime.until()}")
    }

    @Subscription
    fun onChat(event: ChatReceivedEvent.Pre) {
        if (electionOverRegex.matches(event.text)) {
            // When the Election is over, schedule a check every minute until a new mayor is found, then schedule every 20 minutes
            updateScheduler(1.minutes, 20.minutes)
            mayor = null
            minister = null
            currentJerryCandidate = null
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
                    MayorPerks.foxyExtraEventType?.let {
                        appendLine("Foxy Extra Event: ${it.eventName}")
                    }
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
