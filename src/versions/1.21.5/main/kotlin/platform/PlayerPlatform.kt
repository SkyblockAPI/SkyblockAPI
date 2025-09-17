@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package tech.thatgravyboat.skyblockapi.platform

import com.mojang.authlib.GameProfile
import net.minecraft.client.player.LocalPlayer
import java.util.*

actual typealias PlayerSkin = net.minecraft.client.resources.PlayerSkin

actual fun GameProfile.id(): UUID = this.id
actual fun GameProfile.name(): String = this.name
actual fun LocalPlayer.skin(): PlayerSkin = this.skin
