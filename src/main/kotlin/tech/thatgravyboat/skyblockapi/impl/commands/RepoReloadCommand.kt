package tech.thatgravyboat.skyblockapi.impl.commands

import me.owdding.ktmodules.Module
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.repo.RepoEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.sendWithPrefix
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

@Module
object RepoReloadCommand {

    @Subscription
    private fun RegisterSkyblockApiCommandsEvent.onRegister() {
        registerWithCallback("repo reload") {
            Text.of("Reloading RepoLib...").sendWithPrefix()

            RepoAPI.reload { status ->
                McClient.runNextTick {
                    Text.of("RepoLib reloaded with status: $status") {
                        color = TextColor.YELLOW
                    }.sendWithPrefix()

                    RepoEvent.Reload(status).post()
                }
            }
        }
    }
}
