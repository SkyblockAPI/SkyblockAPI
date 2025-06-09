package tech.thatgravyboat.skyblockapi.impl.debug

import com.mojang.brigadier.arguments.StringArgumentType
import me.owdding.ktmodules.Module
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ActionBarReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabListHeaderFooterChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.Pricing
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
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
    private var tabListFooter: Component = Component.empty()
    private var tabListHeader: Component = Component.empty()

    private fun copyMessage(title: String) {
        Text.of("[SkyBlockAPI] Copied $title to clipboard.") {
            this.color = TextColor.YELLOW
        }.send()
    }

    @Subscription(receiveCancelled = true)
    fun onActionBar(event: ActionBarReceivedEvent.Pre) {
        actionbar = event.coloredText
    }

    @Subscription(priority = Int.MIN_VALUE)
    fun onHeaderFooter(event: TabListHeaderFooterChangeEvent) {
        tabListFooter = event.newFooter
        tabListHeader = event.newHeader
    }

    @Subscription
    fun onCommandsRegistration(event: RegisterCommandsEvent) {
        event.register("sbapi") {
            then("price id", StringArgumentType.greedyString()) {
                callback {
                    val id = this.getArgument("id", String::class.java)
                    val price = Pricing.getPrice(id)

                    Text.of("[SkyBlockAPI] Price of $id is ${price.toFormattedString()}.") {
                        this.color = TextColor.YELLOW
                    }.send()
                }
            }
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
                    then("footer") {
                        then("raw") {
                            callback {
                                copyMessage("raw tablist footer")
                                McClient.clipboard = tabListFooter.toJson(ComponentSerialization.CODEC).toPrettyString()
                            }
                        }

                        callback {
                            copyMessage("tablist footer")
                            McClient.clipboard = tabListFooter.stripped
                        }
                    }

                    then("header") {
                        then("raw") {
                            callback {
                                copyMessage("raw tablist header")
                                McClient.clipboard = tabListHeader.toJson(ComponentSerialization.CODEC).toPrettyString()
                            }
                        }

                        callback {
                            copyMessage("tablist header")
                            McClient.clipboard = tabListHeader.stripped
                        }
                    }

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
            then("folder") {
                val gameDir = McClient.self.gameDirectory.toPath()

                listOf("config", "mods", "logs").forEach {
                    then(it) {
                        callback {
                            McClient.openUri(gameDir.resolve(it).toUri())
                        }
                    }
                }

                callback {
                    McClient.openUri(gameDir.toUri())
                }
            }
        }
    }
}
