package tech.thatgravyboat.skyblockapi.impl

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.utils.regex.Destructured
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.matchWhen

@Module
internal object ProfileDeleter {

    private val group = RegexGroup.CHAT.group("profile_delete")
    private val deleteRegex = group.create("delete", "Done! Your (?<name>.*) profile was deleted!")
    private val wipeRegex = group.create("wipe", "Your SkyBlock Profile (?<name>.*) has been wiped .*")

    @Subscription
    fun onChat(event: ChatReceivedEvent.Pre) {
        matchWhen(event.text) {
            case(deleteRegex, "name", action = ::handleDelete)
            case(wipeRegex, "name", action = ::handleDelete)
        }
    }

    private fun handleDelete(destructured: Destructured) {
        val (profileName) = destructured
        StoredProfileData.allProfileData.forEach { it.removeProfile(profileName) }
    }

}
