package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.resources.ResourceLocation
import net.msrandom.stub.Stub

@Stub expect fun AbstractClientPlayer.skin(): PlayerSkin
@Stub expect val PlayerSkin.textureUrl: String?
@Stub expect val PlayerSkin.texture: ResourceLocation?
@Stub expect val PlayerSkin.capeTexture: ResourceLocation?
@Stub expect val PlayerSkin.elytraTexture: ResourceLocation?
@Stub expect val PlayerSkin.secure: Boolean
@Stub expect val PlayerSkin.model: Model

@Stub expect class PlayerSkin

enum class Model {
    SLIM,
    WIDE,
}
