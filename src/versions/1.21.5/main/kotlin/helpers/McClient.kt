@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package tech.thatgravyboat.skyblockapi.helpers

import com.mojang.authlib.minecraft.MinecraftSessionService
import com.mojang.blaze3d.platform.Window
import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
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
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.level.GameType
import net.minecraft.world.scores.DisplaySlot
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.utils.McVersion
import tech.thatgravyboat.skyblockapi.utils.McVersionGroup
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import java.net.URI
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

actual object McClient {

    private val tabListComparator: Comparator<PlayerInfo> = compareBy(
        { it.gameMode == GameType.SPECTATOR },
        { it.team?.name ?: "" },
        { it.profile.name.lowercase() },
    )

    actual val isDev = FabricLoader.getInstance().isDevelopmentEnvironment
    actual val config: Path = FabricLoader.getInstance().configDir

    actual val mcVersionGroup: McVersionGroup get() = McVersionGroup.entries.first { it.isActive }
    actual val mcVersion: McVersion get() = McVersion.entries.first { it.isActive }
    actual val version: String = SharedConstants.getCurrentVersion().name

    actual val sessionService: MinecraftSessionService get() = self.minecraftSessionService
    actual val self: Minecraft get() = Minecraft.getInstance()
    actual val connection: ClientPacketListener? get() = self.connection

    actual val window: Window get() = self.window
    actual val windowHandle: Long get() = window.window

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

    actual fun runOrSchedule(action: () -> Unit) {
        self.executeIfPossible(action)
    }

    @RemoveNextVersion(ReplaceWith("runNextTick(action)"))
    fun tell(action: () -> Unit) = runNextTick(action)

    actual fun playSound(sound: SoundEvent, volume: Float, pitch: Float) {
        McPlayer.self?.playSound(sound, volume, pitch)
    }

    actual fun setTitle(title: Component, subtitle: Component?, fadeInTime: Float, stayTime: Float, fadeOutTime: Float) {
        gui.setTimes((fadeInTime * 20).toInt(), (stayTime * 20).toInt(), (fadeOutTime * 20).toInt())
        gui.setSubtitle(subtitle ?: CommonText.EMPTY)
        gui.setTitle(title)
    }

    actual fun setScreenAsync(screen: () -> Screen?) = runNextTick {
        val next = screen()
        (self.screen as? AbstractContainerScreen<*>)?.onClose()
        self.setScreen(next)
    }

    /** Bad because with this method the screen gets init too early **/
    @RemoveNextVersion(ReplaceWith("setScreenAsync { screen }"))
    fun setScreenAsync(screen: Screen?) = runNextTick { self.setScreen(screen) }

    actual fun setScreen(screen: Screen?) {
        if (self.screen is ChatScreen) {
            setScreenAsync(screen)
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

    actual fun registerClientReloadListener(id: ResourceLocation, listener: PreparableReloadListener) {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(ReloadListenerWrapper(id, listener))
    }

    private data class ReloadListenerWrapper(
        val id: ResourceLocation,
        val original: PreparableReloadListener,
    ) : IdentifiableResourceReloadListener {
        override fun getFabricId(): ResourceLocation = id

        override fun reload(
            barrier: PreparableReloadListener.PreparationBarrier,
            manager: ResourceManager,
            backgroundExecutor: Executor,
            gameExecutor: Executor,
        ): CompletableFuture<Void> = original.reload(barrier, manager, backgroundExecutor, gameExecutor)
    }

}

