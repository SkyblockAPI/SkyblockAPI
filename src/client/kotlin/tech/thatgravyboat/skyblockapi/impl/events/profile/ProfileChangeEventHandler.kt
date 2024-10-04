package tech.thatgravyboat.skyblockapi.impl.events.profile

import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileChangeEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import java.util.*

@Module
object ProfileChangeEventHandler {

    private val regexGroup = RegexGroup.CHAT.group("profile.change")

    // §8Profile ID: 0b7f27ad-fea5-4da0-9886-32913feb60b7
    private val profileIdChangeRegex = regexGroup.create(
        "id",
        "Profile ID: (?<id>[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12})"
    )

    // §aYour profile was changed to: §eWatermelon
    // §aYour profile was changed to: §ePomegranate§b (Co-op)
    private val profileChangeRegex = regexGroup.createList(
        "name",
        "Your profile was changed to: (?<name>.+) \\(Co-op\\)",
        "Your profile was changed to: (?<name>.+)"
    )

    private var lastProfileName: String? = null
    private var lastProfileId: UUID? = null
    private var lastProfileChange: Long = 0

    @Subscription
    fun onChatReceived(event: ChatReceivedEvent) {
        profileChangeRegex.match(event.text, "name") { (name) ->
            lastProfileName = name
            lastProfileChange = System.currentTimeMillis()
        }
        profileIdChangeRegex.match(event.text, "id") { (id) ->
            lastProfileId = UUID.fromString(id)
            if (lastProfileChange + 3000 > System.currentTimeMillis()) {
                lastProfileChange = 0
                val profileId = lastProfileId ?: return@match
                val profileName = lastProfileName ?: return@match
                ProfileChangeEvent(profileId, profileName).post(SkyBlockAPI.eventBus)
            }
            lastProfileChange = 0
        }
    }
}
