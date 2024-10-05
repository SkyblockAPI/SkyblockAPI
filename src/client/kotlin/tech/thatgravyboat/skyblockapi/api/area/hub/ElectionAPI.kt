package tech.thatgravyboat.skyblockapi.api.area.hub

import tech.thatgravyboat.skyblockapi.api.data.Candidate
import tech.thatgravyboat.skyblockapi.api.data.ElectionJson
import tech.thatgravyboat.skyblockapi.api.data.Perk
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.info.MayorUpdateEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.Scheduling
import tech.thatgravyboat.skyblockapi.utils.http.Http
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import java.util.concurrent.ScheduledFuture
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

private const val url = "https://api.hypixel.net/v2/resources/skyblock/election"

@Module
object ElectionAPI {

    private val chatGroup = RegexGroup.CHAT.group("election")
    private val electionOverRegex = chatGroup.create("electionOver", "The election is over!")

    private var scheduler: ScheduledFuture<*>? = null
    private var response: ElectionJson? = null

    private var currentMayor: Candidate? = null
    private var currentMinister: Candidate? = null

    private var lastMayor: Candidate? = null

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
        val result = Http.getResult<ElectionJson>(
            url = url,
            errorFactory = ::RuntimeException,
        )
        response = result.getOrNull() ?: return

        if (lastMayor != currentMayor) {
            handleResponse()
            currentMayor?.let { MayorUpdateEvent(it, currentMinister).post() }
            lastMayor = currentMayor

            if (newSchedulerTime != null) {
                updateScheduler(newSchedulerTime)
            }
        }
    }

    private fun handleResponse() {
        val mayor = response?.mayor ?: return

        currentMayor = Candidate.getCandidate(mayor.name)
        currentMinister = mayor.minister?.let { Candidate.getCandidate(it.name) }

        Perk.reset()
        mayor.perks.forEach { perk ->
            Perk.getPerk(perk.name)?.active = true
        }
        mayor.minister?.perk?.let { Perk.getPerk(it.name)?.active = true }
    }

    @Subscription
    fun onChat(event: ChatReceivedEvent) {
        if (electionOverRegex.matches(event.text)) {
            // When the Election is over, schedule a check every minute until a new mayor is found, then schedule every 20 minutes
            updateScheduler(1.minutes, 20.minutes)
        }
    }


}
