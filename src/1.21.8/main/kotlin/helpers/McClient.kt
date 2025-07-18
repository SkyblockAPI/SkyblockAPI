package tech.thatgravyboat.skyblockapi.helpers

import com.mojang.blaze3d.platform.Window
import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.SharedConstants
import net.minecraft.Util
import net.minecraft.client.Minecraft
import net.minecraft.client.Options
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.components.ChatComponent
import net.minecraft.client.gui.components.toasts.ToastManager
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket
import net.minecraft.world.level.GameType
import net.minecraft.world.scores.DisplaySlot
import java.net.URI
import java.nio.file.Path

actual object McClient {

    private val tabListComparator: Comparator<PlayerInfo> = compareBy(
        { it.gameMode == GameType.SPECTATOR },
        { it.team?.name ?: "" },
        { it.profile.name.lowercase() },
    )

    actual val isDev = FabricLoader.getInstance().isDevelopmentEnvironment
    actual val config: Path = FabricLoader.getInstance().configDir
    actual val version: String = SharedConstants.getCurrentVersion().name()

    actual val self: Minecraft get() = Minecraft.getInstance()
    actual val connection: ClientPacketListener? get() = self.connection

    actual val window: Window by self::window

    actual var clipboard: String
        get() = self.keyboardHandler.clipboard
        set(value) {
            self.keyboardHandler.clipboard = value
        }

    actual val mouse: Pair<Double, Double>
        get() = Pair(
            self.mouseHandler.xpos() * (window.guiScaledWidth / window.screenWidth.coerceAtLeast(1).toDouble()),
            self.mouseHandler.ypos() * (window.guiScaledHeight / window.screenHeight.coerceAtLeast(1).toDouble()),
        )

    actual val tablist: List<PlayerInfo>
        get() = connection
            ?.listedOnlinePlayers
            ?.sortedWith(tabListComparator)
            ?: emptyList()

    actual val players: List<PlayerInfo>
        get() = tablist.filter { it.profile.id.version() == 4 }

    actual val scoreboard: Collection<Component>
        get() {
            val scoreboard = self.level?.scoreboard ?: return emptyList()
            val objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR) ?: return emptyList()
            return scoreboard.listPlayerScores(objective)
                .sortedBy { -it.value() }
                .map {
                    val team = scoreboard.getPlayersTeam(it.owner())
                    Component.empty().also { main ->
                        team?.playerPrefix?.apply { siblings.forEach { sibling -> main.append(sibling) } }
                        team?.playerSuffix?.apply { siblings.forEach { sibling -> main.append(sibling) } }
                    }
                }
        }

    actual val scoreboardTitle get() = self.level?.scoreboard?.getDisplayObjective(DisplaySlot.SIDEBAR)?.displayName
    actual val serverCommands: CommandDispatcher<out SharedSuggestionProvider>? get() = connection?.commands

    actual val toasts: ToastManager get() = self.toastManager
    actual val gui: Gui get() = self.gui
    actual val chat: ChatComponent get() = gui.chat
    actual val options: Options get() = self.options

    actual fun openUri(uri: String): Boolean = runCatching {
        openUri(URI.create(uri))
    }.isSuccess

    actual fun openUri(uri: URI) {
        Util.getPlatform().openUri(uri)
    }

    actual fun runNextTick(action: () -> Unit) {
        self.schedule(action)
    }

    actual fun setScreenAsync(screen: () -> Screen?) = runNextTick { self.setScreen(screen()) }

    actual fun setScreen(screen: Screen?) {
        if (self.screen is ChatScreen) {
            setScreenAsync { screen }
        } else {
            self.setScreen(screen)
        }
    }

    actual fun sendCommand(command: String) {
        connection?.send(ServerboundChatCommandPacket(command.removePrefix("/")))
    }

    /** Sends a command that first goes through client side commands, and then server commands */
    actual fun sendClientCommand(command: String) {
        connection?.sendCommand(command.removePrefix("/"))
    }

}

