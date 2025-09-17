package tech.thatgravyboat.skyblockapi.platform

import com.mojang.authlib.GameProfile
import net.minecraft.client.player.AbstractClientPlayer
import java.util.*

expect fun GameProfile.id(): UUID
expect fun GameProfile.name(): String
expect fun AbstractClientPlayer.skin(): PlayerSkin
expect val PlayerSkin.textureUrl: String?

expect class PlayerSkin
