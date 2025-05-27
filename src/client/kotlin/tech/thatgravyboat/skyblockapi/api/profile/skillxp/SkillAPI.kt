package tech.thatgravyboat.skyblockapi.api.profile.skillxp

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ActionBarReceivedEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup

@Module
object SkillAPI {
    // §63,538/3,163❤     §3+87.8 Mining (127,630,594/0)     §236,127/50k Drill Fuel
    val regex = RegexGroup.ACTIONBAR_WIDGET

    @Subscription
    fun onActionbar(event: ActionBarReceivedEvent.Pre) {
    }
}
