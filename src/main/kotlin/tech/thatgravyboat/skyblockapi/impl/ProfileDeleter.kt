package tech.thatgravyboat.skyblockapi.impl

import com.mojang.brigadier.arguments.StringArgumentType
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.utils.regex.Destructured
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.matchWhen
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.sendWithPrefix
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

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

    @Subscription
    fun onCommand(event: RegisterSkyblockApiCommandsEvent) {
        event.register("profile delete") {
            thenCallback("profileName", StringArgumentType.string()) {
                val profileName = argument<String>("profileName")
                handleDelete(profileName)
                Text.of("Deleted all profile data for ") {
                    color = TextColor.YELLOW
                    append(profileName, TextColor.GOLD)
                    append("!")
                }.sendWithPrefix()
            }
        }
    }

    private fun handleDelete(destructured: Destructured) {
        handleDelete(destructured.component1())
    }

    private fun handleDelete(profileName: String) {
        StoredProfileData.allProfileData.forEach { it.removeProfile(profileName) }
    }

}
