@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.resources.ResourceLocation
import net.minecraft.client.resources.PlayerSkin as MinecraftPlayerSkin

actual typealias PlayerSkin = MinecraftPlayerSkin

actual fun AbstractClientPlayer.skin(): PlayerSkin = this.skin
actual val PlayerSkin.textureUrl: String? get() = this.textureUrl()
actual val PlayerSkin.texture: ResourceLocation? get() = this.texture
actual val PlayerSkin.capeTexture: ResourceLocation? get() = this.capeTexture
actual val PlayerSkin.elytraTexture: ResourceLocation? get() = this.elytraTexture
actual val PlayerSkin.secure: Boolean get() = this.secure
actual val PlayerSkin.model: Model get() = this.model().toPlatformModel()

fun MinecraftPlayerSkin.Model.toPlatformModel() = when (this) {
    MinecraftPlayerSkin.Model.SLIM -> Model.SLIM
    MinecraftPlayerSkin.Model.WIDE -> Model.WIDE
}
