@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.resources.ResourceLocation

actual typealias PlayerSkin = net.minecraft.client.resources.PlayerSkin

actual val AbstractClientPlayer.skin: PlayerSkin get() = this.skin
actual val PlayerSkin.textureUrl: String? get() = this.textureUrl()
actual val PlayerSkin.texture: ResourceLocation? get() = this.texture
actual val PlayerSkin.capeTexture: ResourceLocation? get() = this.capeTexture
actual val PlayerSkin.elytraTexture: ResourceLocation? get() = this.elytraTexture
actual val PlayerSkin.secure: Boolean get() = this.secure
actual val PlayerSkin.model: Model get() = this.model().toPlatformModel()

fun net.minecraft.client.resources.PlayerSkin.Model.toPlatformModel() = when (this) {
    net.minecraft.client.resources.PlayerSkin.Model.SLIM -> Model.SLIM
    net.minecraft.client.resources.PlayerSkin.Model.WIDE -> Model.WIDE
}
