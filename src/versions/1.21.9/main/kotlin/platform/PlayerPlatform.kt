@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package tech.thatgravyboat.skyblockapi.platform

import com.mojang.authlib.GameProfile
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.core.ClientAsset
import java.util.*

actual typealias PlayerSkin = net.minecraft.world.entity.player.PlayerSkin

actual val PlayerSkin.textureUrl: String? get() = (this.body() as? ClientAsset.DownloadedTexture)?.url()

actual fun GameProfile.id(): UUID = this.id
actual fun GameProfile.name(): String = this.name
actual fun AbstractClientPlayer.skin(): PlayerSkin = this.skin
