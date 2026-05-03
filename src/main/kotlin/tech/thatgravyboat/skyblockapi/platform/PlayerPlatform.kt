package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.core.ClientAsset
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.PlayerModelType as MinecraftPlayerModelType
import net.minecraft.world.entity.player.PlayerSkin as MinecraftPlayerSkin

fun AbstractClientPlayer.skin(): PlayerSkin = this.skin

val PlayerSkin.textureUrl: String?
    get() = (this.body() as? ClientAsset.DownloadedTexture)?.url()

val PlayerSkin.texture: Identifier?
    get() = this.body().id()

val PlayerSkin.capeTexture: Identifier?
    get() = this.cape()?.id()

val PlayerSkin.elytraTexture: Identifier?
    get() = this.elytra()?.id()

val PlayerSkin.secure: Boolean get() = this.secure
val PlayerSkin.model: Model get() = this.model().toPlatformModel()


typealias PlayerSkin = MinecraftPlayerSkin

enum class Model {
    SLIM,
    WIDE,
}

fun MinecraftPlayerModelType.toPlatformModel() = when (this) {
    MinecraftPlayerModelType.SLIM -> Model.SLIM
    MinecraftPlayerModelType.WIDE -> Model.WIDE
}
