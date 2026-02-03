package tech.thatgravyboat.skyblockapi.helpers

import com.mojang.authlib.minecraft.MinecraftSessionService
import com.mojang.blaze3d.platform.Window
import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.fabric.api.resource.v1.ResourceLoader
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.SharedConstants
import net.minecraft.client.Minecraft
import net.minecraft.client.Options
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.components.ChatComponent
import net.minecraft.client.gui.components.toasts.ToastManager
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.level.GameType
import net.minecraft.world.scores.DisplaySlot
import tech.thatgravyboat.skyblockapi.utils.McVersion
import tech.thatgravyboat.skyblockapi.utils.McVersionGroup
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import java.net.URI
import java.nio.file.Path

object McClient {

    private val tabListComparator: Comparator<PlayerInfo> = compareBy(
        { it.gameMode == GameType.SPECTATOR },
        { it.team?.name ?: "" },
        { it.profile.name.lowercase() },
    )

    val isDev = FabricLoader.getInstance().isDevelopmentEnvironment
    val config: Path = FabricLoader.getInstance().configDir

    val mcVersionGroup: McVersionGroup get() = McVersionGroup.entries.first { it.isActive }
    val mcVersion: McVersion get() = McVersion.entries.first { it.isActive }

    val version: String = SharedConstants.getCurrentVersion().name()

    val sessionService: MinecraftSessionService
        get() = self.services().sessionService()

    val self: Minecraft get() = Minecraft.getInstance()
    val connection: ClientPacketListener? get() = self.connection

    val window: Window by self::window
    val windowHandle: Long
        get() = window.handle()

    var clipboard: String
        get() = self.keyboardHandler.clipboard
        set(value) {
            self.keyboardHandler.clipboard = value
        }

    val mouse: Pair<Double, Double>
        get() = Pair(
            self.mouseHandler.xpos() * (window.guiScaledWidth / window.screenWidth.coerceAtLeast(1).toDouble()),
            self.mouseHandler.ypos() * (window.guiScaledHeight / window.screenHeight.coerceAtLeast(1).toDouble()),
        )

    val tablist: List<PlayerInfo>
        get() = connection
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
                .sortedBy { -it.value() }
                .map {
                    val ownerName = it.ownerName()
                    val team = scoreboard.getPlayersTeam(it.owner())
                    if (team == null) {
                        ownerName.copy()
                    } else {
                        Component.empty().also { main ->
                            main.append(team.playerPrefix)
                            if (ownerName.stripped.isNotEmpty()) main.append(ownerName)
                            main.append(team.playerSuffix)
                        }
                    }
                }
        }

    val scoreboardTitle get() = self.level?.scoreboard?.getDisplayObjective(DisplaySlot.SIDEBAR)?.displayName
    val serverCommands: CommandDispatcher<out SharedSuggestionProvider>? get() = connection?.commands

    val toasts: ToastManager get() = self.toastManager
    val gui: Gui get() = self.gui
    val chat: ChatComponent get() = gui.chat
    val options: Options get() = self.options

    fun openUri(uri: String): Boolean = runCatching {
        openUri(URI.create(uri))
    }.isSuccess

    fun openUri(uri: URI) {
        /*? if > 1.21.10 {*/net.minecraft.util.Util/*?} else {*//*net.minecraft.Util*//*?}*/
            .getPlatform().openUri(uri)
    }

    fun runNextTick(action: () -> Unit) {
        self.schedule(action)
    }

    fun runOrNextTick(action: () -> Unit) {
        self.executeIfPossible(action)
    }

    fun playSound(sound: SoundEvent, volume: Float = 1f, pitch: Float = 1f) {
        McPlayer.self?.playSound(sound, volume, pitch)
    }

    fun setTitle(title: Component, subtitle: Component? = null, fadeInTime: Float = 1f, stayTime: Float = 3f, fadeOutTime: Float = 1f) {
        gui.setTimes((fadeInTime * 20).toInt(), (stayTime * 20).toInt(), (fadeOutTime * 20).toInt())
        gui.setSubtitle(subtitle ?: CommonText.EMPTY)
        gui.setTitle(title)
    }

    fun setScreenAsync(screen: () -> Screen?) = runNextTick {
        val next = screen()
        (self.screen as? AbstractContainerScreen<*>)?.onClose()
        self.setScreen(next)
    }

    @Deprecated("Use setScreenAsync to avoid creating screens off the main thread")
    fun setScreenAsync(screen: Screen?) = runNextTick {
        (self.screen as? AbstractContainerScreen<*>)?.onClose()
        self.setScreen(screen)
    }

    fun setScreen(screen: Screen?) {
        if (self.screen is ChatScreen) {
            setScreenAsync { screen }
        } else {
            self.setScreen(screen)
        }
    }

    fun sendCommand(command: String) {
        connection?.send(ServerboundChatCommandPacket(command.removePrefix("/")))
    }

    /** Sends a command that first goes through client side commands, and then server commands */
    fun sendClientCommand(command: String) {
        connection?.sendCommand(command.removePrefix("/"))
    }

    fun registerClientReloadListener(id: Identifier, listener: PreparableReloadListener) {
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(id, listener)
    }
}

