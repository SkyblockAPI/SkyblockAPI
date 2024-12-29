package tech.thatgravyboat.skyblockapi.helpers

import com.mojang.blaze3d.platform.Window
import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.client.Options
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.components.ChatComponent
import net.minecraft.client.gui.components.toasts.ToastManager
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket
import net.minecraft.world.level.GameType
import net.minecraft.world.scores.DisplaySlot

object McClient {

    private val tabListComparator: Comparator<PlayerInfo> = compareBy(
        { it.gameMode == GameType.SPECTATOR },
        { it.team?.name ?: "" },
        { it.profile.name.lowercase() },
    )

    val isDev = FabricLoader.getInstance().isDevelopmentEnvironment

    val self: Minecraft get() = Minecraft.getInstance()

    val window: Window
        get() = self.window

    var clipboard: String?
        get() = self.keyboardHandler?.clipboard
        set(value) {
            self.keyboardHandler?.clipboard = value
        }

    val mouse: Pair<Double, Double>
        get() = Pair(
            self.mouseHandler.xpos() * (window.guiScaledWidth / window.screenWidth.coerceAtLeast(1).toDouble()),
            self.mouseHandler.ypos() * (window.guiScaledHeight / window.screenHeight.coerceAtLeast(1).toDouble())
        )

    val tablist: List<PlayerInfo>
        get() = self.connection
            ?.listedOnlinePlayers
            ?.sortedWith(tabListComparator)
            ?: emptyList()

    val players: List<PlayerInfo>
        get() = tablist.filter { it.profile.id.version() == 4 }

    val scoreboard: Collection<Component>
        get() {
            val scoreboard = self.level?.scoreboard ?: return emptyList()
            val objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR) ?: return emptyList()
            return scoreboard.listPlayerScores(objective)
                .sortedBy { -it.value }
                .map {
                    val team = scoreboard.getPlayersTeam(it.owner)
                    Component.empty().also { main ->
                        team?.playerPrefix?.apply { siblings.forEach { sibling -> main.append(sibling) } }
                        team?.playerSuffix?.apply { siblings.forEach { sibling -> main.append(sibling) } }
                    }
                }
        }

    val scoreboardTitle get() = self.level?.scoreboard?.getDisplayObjective(DisplaySlot.SIDEBAR)?.displayName
    val serverCommands: CommandDispatcher<SharedSuggestionProvider>? get() = self.connection?.commands

    val toasts: ToastManager get() = self.toastManager
    val gui: Gui get() = self.gui
    val chat: ChatComponent get() = gui.chat
    val options: Options get() = self.options

    fun tell(action: () -> Unit) {
        self.schedule(action)
    }

    fun setScreen(screen: Screen?) {
        if (self.screen is ChatScreen) {
            tell { self.setScreen(screen) }
        } else {
            self.setScreen(screen)
        }
    }

    fun sendCommand(command: String) {
        self.connection?.send(ServerboundChatCommandPacket(command.removePrefix("/")))
    }

}

