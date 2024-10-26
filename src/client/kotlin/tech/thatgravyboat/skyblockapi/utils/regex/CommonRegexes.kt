package tech.thatgravyboat.skyblockapi.utils.regex

import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findGroup
import java.util.*

@Module
internal object CommonRegexes {

    private val chatGroup = RegexGroup.CHAT

    val viewProfileRegex = chatGroup.create(
        "viewprofile",
        "^/viewprofile (?<uuid>.+)"
    )

    fun getUuidFromViewProfile(component: Component): UUID? {
        val clickEvent = component.style.clickEvent ?: return null
        if (clickEvent.action != ClickEvent.Action.RUN_COMMAND) return null
        val uuidString = viewProfileRegex.findGroup(clickEvent.value, "uuid") ?: return null
        return UUID.fromString(uuidString)
    }

}
