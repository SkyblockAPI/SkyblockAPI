package tech.thatgravyboat.skyblockapi.impl.debug

import net.minecraft.network.chat.ComponentSerialization
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

    private fun copyMessage(title: String) {
        Text.of("[SkyBlockAPI] Copied $title to clipboard.") {
            this.color = TextColor.YELLOW
        }.send()
    }

    @Subscription(priority = Int.MIN_VALUE)
    fun onActionBar(event: ActionBarReceivedEvent) {
        actionbar = event.coloredText
    }

    @Subscription
    fun onCommandsRegistration(event: RegisterCommandsEvent) {
        event.register("sbapi") {
            then("copy") {
                then("scoreboard") {
                    then("raw") {
                        callback {
                            copyMessage("raw scoreboard")
                            McClient.clipboard = McClient.scoreboard.joinToString("\n") {
                                it.toJson(ComponentSerialization.CODEC).toPrettyString()
                            }
                        }
                    }

                    callback {
                        copyMessage("scoreboard")
                        McClient.clipboard = McClient.scoreboard.joinToString("\n") { it.stripped }
                    }
                }

                then("tablist") {
                    then("raw") {
                        callback {
                            copyMessage("raw tablist")
                            McClient.clipboard = McClient.tablist.joinToString("\n") {
                                it.displayName.toJson(ComponentSerialization.CODEC).toPrettyString()
                            }
                        }
                    }

                    callback {
                        copyMessage("tablist")
                        McClient.clipboard = McClient.tablist.joinToString("\n") { it.displayName.stripped }
                    }
                }

                then("item") {
                    callback {
                        copyMessage("item")
                        McClient.clipboard = McPlayer.heldItem.toJson(ItemStack.CODEC).toPrettyString()
                    }
                }

                then("actionbar") {
                    callback {
                        copyMessage("actionbar")
                        McClient.clipboard = actionbar
                    }
                }
            }
        }
    }
}
