@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.core.ClientAsset
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.PlayerModelType
import net.minecraft.world.entity.player.PlayerSkin as MinecraftPlayerSkin

actual typealias PlayerSkin = MinecraftPlayerSkin

actual val PlayerSkin.textureUrl: String? get() = (this.body() as? ClientAsset.DownloadedTexture)?.url()

actual fun AbstractClientPlayer.skin(): PlayerSkin = this.skin
actual val PlayerSkin.texture: Identifier? get() = this.body().id()
actual val PlayerSkin.capeTexture: Identifier? get() = this.cape()?.id()
actual val PlayerSkin.elytraTexture: Identifier? get() = this.elytra()?.id()
actual val PlayerSkin.secure: Boolean get() = this.secure
actual val PlayerSkin.model: Model get() = this.model().toPlatformModel()

fun PlayerModelType.toPlatformModel() = when (this) {
    PlayerModelType.SLIM -> Model.SLIM
    PlayerModelType.WIDE -> Model.WIDE
}
