package tech.thatgravyboat.skyblockapi.impl.debug

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ActionBarReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.mc.displayName
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

@Module
object DebugCommands {

    private var actionbar: String = ""

    @Subscription(priority = Int.MIN_VALUE)
    fun onActionBar(event: ActionBarReceivedEvent) {
        actionbar = event.coloredText
    }

    @Subscription
    fun onCommandsRegistration(event: RegisterCommandsEvent) {
        event.register("sbapi") {
            then("copy") {
                then("scoreboard") {
                    callback {
                        Text.of("[SkyBlockAPI] Copied scoreboard to clipboard.") {
                            this.color = TextColor.YELLOW
                        }.send()
                        McClient.clipboard = McClient.scoreboard?.joinToString("\n") ?: ""
                    }
                }

                then("tablist") {
                    callback {
                        Text.of("[SkyBlockAPI] Copied tablist to clipboard.") {
                            this.color = TextColor.YELLOW
                        }.send()
                        McClient.clipboard = McClient.tablist.joinToString("\n") { it.displayName.stripped }
                    }
                }

                then("item") {
                    callback {
                        Text.of("[SkyBlockAPI] Copied item to clipboard.") {
                            this.color = TextColor.YELLOW
                        }.send()
                        McClient.clipboard = McPlayer.heldItem.toJson(ItemStack.CODEC).toPrettyString()
                    }
                }

                then("actionbar") {
                    callback {
                        Text.of("[SkyBlockAPI] Copied actionbar to clipboard.") {
                            this.color = TextColor.YELLOW
                        }.send()
                        McClient.clipboard = actionbar
                    }
                }
            }
        }
    }
}
