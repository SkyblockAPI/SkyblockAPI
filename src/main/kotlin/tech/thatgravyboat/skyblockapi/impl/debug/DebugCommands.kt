package tech.thatgravyboat.skyblockapi.impl.debug

import com.google.gson.JsonArray
import com.mojang.brigadier.arguments.StringArgumentType
import me.owdding.ktmodules.Module
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.biome.Biome
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ActionBarReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabListHeaderFooterChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent.Companion.argument
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.ItemData
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.Pricing
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.platform.identifier
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.json.Json.toJson
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import tech.thatgravyboat.skyblockapi.utils.mc.displayName
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.style
import kotlin.io.path.createDirectories

@Module
public object DebugCommands {

    private var actionbar: String = ""
    private var tabListFooter: Component = Component.empty()
    private var tabListHeader: Component = Component.empty()

    private fun copyMessage(title: String) {
        Text.sendDebug("Copied $title to clipboard.")
    }

    private fun Component?.toPrettyJson(): String = this?.toJson(ComponentSerialization.CODEC).toPrettyString()

    @Subscription(receiveCancelled = true)
    internal fun onActionBar(event: ActionBarReceivedEvent.Pre) {
        actionbar = event.coloredText
    }

    @Subscription(priority = Int.MIN_VALUE)
    internal fun onHeaderFooter(event: TabListHeaderFooterChangeEvent) {
        tabListFooter = event.newFooter
        tabListHeader = event.newHeader
    }

    @Subscription
    private fun RegisterSkyblockApiCommandsEvent.onCommandsRegistration() {
        register("price") {
            thenCallback("id", StringArgumentType.greedyString()) {
                val id = argument<String>("id")
                val price = Pricing.getPrice(id)

                Text.sendDebug("Price of $id is ${price.toFormattedString()}.")
            }
        }
        register("itemdata") {
            thenCallback("id", StringArgumentType.greedyString()) {
                val id = argument<String>("id")
                val itemData = ItemData.getItemData(id) ?: run {
                    Text.sendDebug("ItemData for $id not found.")
                    return@thenCallback
                }

                McClient.clipboard = itemData.toString()

                Text.sendDebug("ItemData of $id copied to clipboard.")
            }
            thenCallback("all") {
                McClient.clipboard = ItemData.data.toString()

                Text.sendDebug("Full ItemData copied to clipboard.")
            }
        }
        register("copy") {
            then("scoreboard") {
                then("title") {
                    thenCallback("raw") {
                        copyMessage("raw scoreboard title")
                        McClient.clipboard = McClient.scoreboardTitle.toPrettyJson()
                    }

                    callback {
                        copyMessage("scoreboard title")
                        McClient.clipboard = McClient.scoreboardTitle?.stripped ?: "null"
                    }
                }
                thenCallback("raw") {
                    copyMessage("raw scoreboard")
                    McClient.clipboard = McClient.scoreboard.joinToString("\n") { it.toPrettyJson() }
                }

                callback {
                    copyMessage("scoreboard")
                    McClient.clipboard = McClient.scoreboard.joinToString("\n") { it.stripped }
                }
            }

            then("tablist") {
                then("footer") {
                    thenCallback("raw") {
                        copyMessage("raw tablist footer")
                        McClient.clipboard = tabListFooter.toPrettyJson()
                    }

                    callback {
                        copyMessage("tablist footer")
                        McClient.clipboard = tabListFooter.stripped
                    }
                }

                then("header") {
                    thenCallback("raw") {
                        copyMessage("raw tablist header")
                        McClient.clipboard = tabListHeader.toPrettyJson()
                    }

                    callback {
                        copyMessage("tablist header")
                        McClient.clipboard = tabListHeader.stripped
                    }
                }

                thenCallback("raw") {
                    copyMessage("raw tablist")
                    McClient.clipboard = McClient.tablist.joinToString("\n") {
                        it.displayName.toPrettyJson()
                    }
                }

                callback {
                    copyMessage("tablist")
                    McClient.clipboard = McClient.tablist.joinToString("\n") { it.displayName.stripped }
                }
            }

            thenCallback("item") {
                copyMessage("item")
                McClient.clipboard = McPlayer.heldItem.toJson(ItemStack.CODEC).toPrettyString()
            }

            thenCallback("actionbar") {
                copyMessage("actionbar")
                McClient.clipboard = actionbar
            }
        }
        register("folder") {
            val gameDir = McClient.self.gameDirectory.toPath()

            listOf("config", "mods", "logs").forEach {
                thenCallback(it) {
                    McClient.openUri(gameDir.resolve(it).toUri())
                }
            }

            thenCallback("chest_dumps") {
                McClient.openUri(gameDir.resolve("config/skyblockapi/chest_dumps").toUri())
            }
        }
        register("save") {
            thenCallback("registries") {
                val outputs = McClient.config.resolve(".skyblock-debug").resolve("registries")
                outputs.createDirectories()

                val connection = McClient.connection ?: return@thenCallback
                val registries = connection.registryAccess().registries()

                registries.forEach { registry ->
                    val location = registry.key().identifier
                    val path = outputs.resolve("${location.namespace}-${location.path.replace("/", "-")}.json")
                    val data = JsonArray()

                    registry.value().keySet().forEach { data.add(it.toString()) }

                    path.toFile().writeText(data.toPrettyString())
                }
            }

            thenCallback("biomes") {
                val outputs = McClient.config.resolve(".skyblock-debug").resolve("biomes")
                outputs.createDirectories()

                val connection = McClient.connection ?: return@thenCallback
                val biomes = connection.registryAccess().lookupOrThrow(Registries.BIOME).entrySet()

                biomes.forEach { (key, biome) ->
                    val location = key.identifier
                    val path = outputs.resolve(location.namespace)
                        .resolve("worldgen")
                        .resolve("biome")
                        .resolve("${location.path}.json")

                    path.parent.createDirectories()

                    path.toFile().writeText(biome.toJson(Biome.DIRECT_CODEC).toPrettyString())
                }

                Text.sendDebug("Saved ${biomes.size} biomes. Click to open folder.") {
                    this.hover = Text.of("Click to open the folder.")
                    this.style {
                        this.withClickEvent(ClickEvent.OpenFile(outputs))
                    }
                }
            }
        }
    }
}
