package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.core.ClientAsset
import net.minecraft.resources.Identifier

//? if > 1.21.8 {
import net.minecraft.world.entity.player.PlayerModelType as MinecraftPlayerModelType
import net.minecraft.world.entity.player.PlayerSkin as MinecraftPlayerSkin
//?} else {
/*import net.minecraft.client.resources.PlayerSkin as MinecraftPlayerSkin
import net.minecraft.client.resources.PlayerSkin.Model as MinecraftPlayerModelType
*///?}

fun AbstractClientPlayer.skin(): PlayerSkin = this.skin

val PlayerSkin.textureUrl: String?
    //? if > 1.21.8 {
    get() = (this.body() as? ClientAsset.DownloadedTexture)?.url()
    //?} else
    /*get() = this.textureUrl()*/

val PlayerSkin.texture: Identifier?
    //? if > 1.21.8 {
    get() = this.body().id()
    //?} else
    /*get() = this.texture*/

val PlayerSkin.capeTexture: Identifier?
    //? if > 1.21.8 {
    get() = this.cape()?.id()
    //?} else
    /*get() = this.capeTexture*/

val PlayerSkin.elytraTexture: Identifier?
    //? if > 1.21.8 {
    get() = this.elytra()?.id()
    //?} else
    /*get() = this.elytraTexture*/

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
