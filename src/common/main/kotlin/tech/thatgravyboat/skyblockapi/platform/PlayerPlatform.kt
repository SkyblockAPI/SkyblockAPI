package tech.thatgravyboat.skyblockapi.platform

import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.resources.ResourceLocation

expect val AbstractClientPlayer.skin: PlayerSkin
expect val PlayerSkin.textureUrl: String?
expect val PlayerSkin.texture: ResourceLocation?
expect val PlayerSkin.capeTexture: ResourceLocation?
expect val PlayerSkin.elytraTexture: ResourceLocation?
expect val PlayerSkin.secure: Boolean
expect val PlayerSkin.model: Model

expect class PlayerSkin

enum class Model {
    SLIM,
    WIDE,
}
