package tech.thatgravyboat.skyblockapi.utils.regex

import me.owdding.ktmodules.Module
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findGroup
import java.util.UUID

@Module
internal object CommonRegexes {

    private val chatGroup = RegexGroup.CHAT

    val viewProfileRegex = chatGroup.create(
        "viewprofile",
        "^/viewprofile (?<uuid>.+)"
    )

    fun getUuidFromViewProfile(component: Component): UUID? {
        val clickEvent = component.style.clickEvent ?: return null
        if (clickEvent !is ClickEvent.RunCommand) return null
        val uuidString = viewProfileRegex.findGroup(clickEvent.command(), "uuid") ?: return null
        return UUID.fromString(uuidString)
    }

}
