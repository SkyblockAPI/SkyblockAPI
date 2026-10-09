package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.core.ClientAsset
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.PlayerModelType as MinecraftPlayerModelType
import net.minecraft.world.entity.player.PlayerSkin as MinecraftPlayerSkin

public fun AbstractClientPlayer.skin(): PlayerSkin = this.skin

public val PlayerSkin.textureUrl: String?
    get() = (this.body() as? ClientAsset.DownloadedTexture)?.url()

public val PlayerSkin.texture: Identifier?
    get() = this.body().id()

public val PlayerSkin.capeTexture: Identifier?
    get() = this.cape()?.id()

public val PlayerSkin.elytraTexture: Identifier?
    get() = this.elytra()?.id()

public val PlayerSkin.secure: Boolean get() = this.secure
public val PlayerSkin.model: Model get() = this.model().toPlatformModel()


public typealias PlayerSkin = MinecraftPlayerSkin

public enum class Model {
    SLIM,
    WIDE,
}

public fun MinecraftPlayerModelType.toPlatformModel() = when (this) {
    MinecraftPlayerModelType.SLIM -> Model.SLIM
    MinecraftPlayerModelType.WIDE -> Model.WIDE
}
