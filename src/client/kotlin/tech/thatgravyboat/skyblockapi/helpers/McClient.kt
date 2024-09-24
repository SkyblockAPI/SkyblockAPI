package tech.thatgravyboat.skyblockapi.helpers

import com.mojang.blaze3d.platform.Window
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.world.level.GameType

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

    val tablist: List<PlayerInfo>
        get() = self.connection
            ?.listedOnlinePlayers
            ?.sortedWith(tabListComparator)
            ?: emptyList()

    val players: List<PlayerInfo>
        get() = tablist.filter { it.profile.id.version() == 4 }

    val mouse: Pair<Double, Double>
        get() = Pair(
            self.mouseHandler.xpos() * (window.guiScaledWidth / window.screenWidth.coerceAtLeast(1).toDouble()),
            self.mouseHandler.ypos() * (window.guiScaledHeight / window.screenHeight.coerceAtLeast(1).toDouble())
        )

}

