package tech.thatgravyboat.skyblockapi.helpers

import com.mojang.blaze3d.platform.Window
import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.toasts.ToastComponent
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.world.level.GameType
import net.minecraft.world.scores.DisplaySlot
import net.minecraft.world.scores.PlayerTeam
import tech.thatgravyboat.skyblockapi.utils.extentions.stripColor
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

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

    val scoreboard: Collection<String>?
        get() {
            val scoreboard = self.level?.scoreboard ?: return null
            val objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR) ?: return null
            return scoreboard.listPlayerScores(objective)
                .sortedBy { -it.value }
                .map { PlayerTeam.formatNameForTeam(scoreboard.getPlayersTeam(it.owner), CommonText.EMPTY) }
                .map { it.stripped }
        }

    val scoreboardTitle get() = self.level?.scoreboard?.getDisplayObjective(DisplaySlot.SIDEBAR)?.displayName?.string?.stripColor()

    val toasts: ToastComponent
        get() = self.toasts

    val serverCommands: CommandDispatcher<SharedSuggestionProvider>?
        get() = self.connection?.commands

    fun tell(action: () -> Unit) {
        self.tell(action)
    }

    fun setScreen(screen: Screen?) {
        if (self.screen is ChatScreen) {
            tell { self.setScreen(screen) }
        } else {
            self.setScreen(screen)
        }
    }

    fun sendCommand(command: String) {
        self.connection?.sendCommand(command)
    }

}

