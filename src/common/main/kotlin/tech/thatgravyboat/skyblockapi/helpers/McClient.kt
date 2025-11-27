package tech.thatgravyboat.skyblockapi.helpers

import com.mojang.authlib.minecraft.MinecraftSessionService
import com.mojang.blaze3d.platform.Window
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.client.Minecraft
import net.minecraft.client.Options
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.components.ChatComponent
import net.minecraft.client.gui.components.toasts.ToastManager
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.PreparableReloadListener
import net.minecraft.sounds.SoundEvent
import net.msrandom.stub.Stub
import tech.thatgravyboat.skyblockapi.utils.McVersion
import tech.thatgravyboat.skyblockapi.utils.McVersionGroup
import java.net.URI
import java.nio.file.Path

@Stub
expect object McClient {
    val isDev: Boolean
    val config: Path

    val sessionService: MinecraftSessionService
    val mcVersionGroup: McVersionGroup
    val mcVersion: McVersion
    val version: String

    val self: Minecraft
    val connection: ClientPacketListener?

    val window: Window
    val windowHandle: Long

    var clipboard: String

    val mouse: Pair<Double, Double>

    val tablist: List<PlayerInfo>

    val players: List<PlayerInfo>

    val scoreboard: Collection<Component>

    val scoreboardTitle: Component?
    val serverCommands: CommandDispatcher<out SharedSuggestionProvider>?

    val toasts: ToastManager
    val gui: Gui
    val chat: ChatComponent
    val options: Options

    fun openUri(uri: String): Boolean

    fun openUri(uri: URI)

    fun runNextTick(action: () -> Unit)

    fun runOrSchedule(action: () -> Unit)

    fun playSound(sound: SoundEvent, volume: Float = 1f, pitch: Float = 1f)

    fun setTitle(title: Component, subtitle: Component? = null, fadeInTime: Float = 1f, stayTime: Float = 3f, fadeOutTime: Float = 1f)

    fun setScreenAsync(screen: () -> Screen?)

    fun setScreen(screen: Screen?)

    fun sendCommand(command: String)

    /** Sends a command that first goes through client side commands, and then server commands */
    fun sendClientCommand(command: String)

    fun registerClientReloadListener(id: ResourceLocation, listener: PreparableReloadListener)
}

