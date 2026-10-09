package tech.thatgravyboat.skyblockapi.helpers

//? >= 26.3
import com.mojang.blaze3d.Blaze3D
import com.mojang.blaze3d.platform.Window
import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.fabric.api.resource.v1.ResourceLoader
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.SharedConstants
import net.minecraft.client.Minecraft
import net.minecraft.client.Options
import net.minecraft.client.gui.Gui
//? >= 26.2
import net.minecraft.client.gui.Hud
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
//? < 26.3
//import net.minecraft.util.Util
import net.minecraft.world.level.GameType
import net.minecraft.world.scores.DisplaySlot
import tech.thatgravyboat.skyblockapi.utils.McVersion
import tech.thatgravyboat.skyblockapi.utils.McVersionGroup
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import java.net.URI
import java.nio.file.Path

public object McClient {

    private val tabListComparator: Comparator<PlayerInfo> = compareBy(
        { it.gameMode == GameType.SPECTATOR },
        { it.team?.name ?: "" },
        { it.profile.name.lowercase() },
    )

    public val isDev: Boolean = FabricLoader.getInstance().isDevelopmentEnvironment
    public val config: Path = FabricLoader.getInstance().configDir

    @Deprecated("Use mcVersion instead")
    @Suppress("DEPRECATION")
    public val mcVersionGroup: McVersionGroup get() = McVersionGroup.entries.first { it.isActive }
    public val mcVersion: McVersion get() = McVersion.entries.first { it.isActive }

    public val version: String = SharedConstants.getCurrentVersion().name()

    //~ if >= 26.3 'MinecraftSessionService' -> 'SessionService'
    public val sessionService: com.mojang.authlib.minecraft.SessionService
        get() = self.services().sessionService()

    public val self: Minecraft get() = Minecraft.getInstance()
    public val connection: ClientPacketListener? get() = self.connection

    public val window: Window by self::window
    public val windowHandle: Long
        get() = window.handle()

    public var clipboard: String
        get() = self.keyboardHandler.clipboard
        set(value) {
            self.keyboardHandler.clipboard = value
        }

    public val mouse: Pair<Double, Double>
        get() = Pair(
            self.mouseHandler.xpos() * (window.guiScaledWidth / window.screenWidth.coerceAtLeast(1).toDouble()),
            self.mouseHandler.ypos() * (window.guiScaledHeight / window.screenHeight.coerceAtLeast(1).toDouble()),
        )

    public val tablist: List<PlayerInfo>
        get() = connection
            ?.listedOnlinePlayers
            ?.sortedWith(tabListComparator)
            ?: emptyList()

    public val players: List<PlayerInfo>
        get() = tablist.filter { it.profile.id.version() == 4 }

    public val scoreboard: Collection<Component>
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

    public val scoreboardTitle: Component? get() = self.level?.scoreboard?.getDisplayObjective(DisplaySlot.SIDEBAR)?.displayName
    public val serverCommands: CommandDispatcher<out SharedSuggestionProvider>? get() = connection?.commands

    public val toasts: ToastManager get() =/*? if >= 26.2 {*/gui.toastManager()/*? } else *///self.toastManager
    public val gui: Gui get() = self.gui

    //? >= 26.2
    public val hud: Hud get() = self.gui.hud
    //~ if >= 26.2 'gui' -> 'hud'
    public val chat: ChatComponent get() = hud.chat
    public val options: Options get() = self.options

    public val isSingleplayer: Boolean get() = /*? if >= 26.2 {*/!self.isMultiplayerServer/*? } else*///self.isSingleplayer

    public fun openUri(uri: String): Boolean = runCatching {
        openUri(URI.create(uri))
    }.isSuccess

    public fun reloadResourcePacks() {
        self.reloadResourcePacks()
    }

    public fun openUri(uri: URI) {
        //? >= 26.3 {
        Blaze3D.openUri(uri)
        //? } else
        //Util.getPlatform().openUri(uri)
    }

    /**
     * Runs the next render tick.
     */
    public fun runNextTick(action: () -> Unit) {
        self.schedule(action)
    }

    /**
     * Runs either on the current or next render tick
     * depending on if it's executed from the render thread.
     */
    public fun runOrNextTick(action: () -> Unit) {
        self.executeIfPossible(action)
    }

    public fun playSound(sound: SoundEvent, volume: Float = 1f, pitch: Float = 1f) {
        McPlayer.self?.playSound(sound, volume, pitch)
    }

    public fun setTitle(title: Component, subtitle: Component? = null, fadeInTime: Float = 1f, stayTime: Float = 3f, fadeOutTime: Float = 1f) {
        //~ if >= 26.2 'gui.' -> 'hud.' {
        hud.setTimes((fadeInTime * 20).toInt(), (stayTime * 20).toInt(), (fadeOutTime * 20).toInt())
        hud.setSubtitle(subtitle ?: CommonText.EMPTY)
        hud.setTitle(title)
        //~ }
    }

    public fun setScreenAsync(screen: () -> Screen?): Unit = runNextTick {
        val next = screen()
        (McScreen.self as? AbstractContainerScreen<*>)?.onClose()
        //~ if >= 26.2 'self' -> 'gui'
        gui.setScreen(next)
    }

    //? < 26.2 {
    /*@Deprecated("Use setScreenAsync to avoid creating screens off the main thread")
    public fun setScreenAsync(screen: Screen?): Unit = runNextTick {
        (self.screen as? AbstractContainerScreen<*>)?.onClose()
        self.setScreen(screen)
    }*///? }

    public fun setScreen(screen: Screen?) {
        if (McScreen.self is ChatScreen) {
            setScreenAsync { screen }
        } else {
            //~ if >= 26.2 'self' -> 'gui'
            gui.setScreen(screen)
        }
    }

    public fun sendCommand(command: String) {
        connection?.send(ServerboundChatCommandPacket(command.removePrefix("/")))
    }

    /** Sends a command that first goes through client side commands, and then server commands */
    public fun sendClientCommand(command: String) {
        connection?.sendCommand(command.removePrefix("/"))
    }

    public fun registerClientReloadListener(id: Identifier, listener: PreparableReloadListener) {
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(id, listener)
    }

    public fun anyModInstalled(modIds: Collection<String>): Boolean = modIds.any { FabricLoader.getInstance().isModLoaded(it) }
    public fun anyModInstalled(vararg modIds: String): Boolean = anyModInstalled(modIds.asList())
}

