package tech.thatgravyboat.skyblockapi.api.area.hub

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.Candidate
import tech.thatgravyboat.skyblockapi.api.data.ElectionJson
import tech.thatgravyboat.skyblockapi.api.data.Perk
import tech.thatgravyboat.skyblockapi.api.data.PerkJson
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.info.MayorUpdateEvent
import tech.thatgravyboat.skyblockapi.utils.Scheduling
import tech.thatgravyboat.skyblockapi.utils.http.Http
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import java.util.concurrent.ScheduledFuture
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

private const val URL = "https://api.hypixel.net/v2/resources/skyblock/election"

@Module
object ElectionAPI {

    private val chatGroup = RegexGroup.CHAT.group("election")
    private val electionOverRegex = chatGroup.create(
        "electionOver", 
        "The election room is now closed\\. Clerk Seraphine is doing a final count of the votes\\.\\.\\.",
    )

    private var scheduler: ScheduledFuture<*>? = null
    var rawData: ElectionJson? = null
        private set

    var currentMayor: Candidate? = null
        private set
    var currentMinister: Candidate? = null
        private set
    var jerryCandidate: Candidate? = null
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
        val result = Http.getResult<ElectionJson>(URL)
        val response = result.getOrNull() ?: return

        if (handleResponse(response)) {
            currentMayor?.let { MayorUpdateEvent(it, currentMinister).post() }

            if (newSchedulerTime != null) {
                updateScheduler(newSchedulerTime)
            }
        }
    }

    private fun handleResponse(response: ElectionJson?): Boolean {
        rawData = response
        val mayor = response?.mayor ?: return false

        val newMayor = Candidate.getCandidate(mayor.name) ?: return false
        if (newMayor == currentMayor) return false
        currentMayor = newMayor
        currentMinister = mayor.minister?.let { Candidate.getCandidate(it.name) }

        Perk.reset()
        mayor.perks.forEach(::handlePerk)
        mayor.minister?.perk?.let(::handlePerk)

        return true
    }

    private fun handlePerk(perk: PerkJson) {
        val perkData = Perk.getPerk(perk.name) ?: return
        perkData.active = true
        perkData.description = perk.description
    }

    @Subscription
    fun onChat(event: ChatReceivedEvent.Pre) {
        if (electionOverRegex.matches(event.text)) {
            // When the Election is over, schedule a check every minute until a new mayor is found, then schedule every 20 minutes
            updateScheduler(1.minutes, 20.minutes)
        }
    }

}
