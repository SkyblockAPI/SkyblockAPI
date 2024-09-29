package tech.thatgravyboat.skyblockapi.impl

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.mc.displayName
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@Module
object DebugCommands {

    @Subscription
    fun onCommandsRegistration(event: RegisterCommandsEvent) {
        event.register("sbapi") {
            then("copy") {
                then("scoreboard") {
                    callback {
                        McClient.clipboard = McClient.scoreboard?.joinToString("\n") ?: ""
                    }
                }

                then("tablist") {
                    callback {
                        McClient.clipboard = McClient.tablist.joinToString("\n") { it.displayName.stripped }
                    }
                }
            }
        }
    }
}
