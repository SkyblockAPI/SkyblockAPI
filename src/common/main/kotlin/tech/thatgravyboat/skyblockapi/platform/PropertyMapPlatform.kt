package tech.thatgravyboat.skyblockapi.platform

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.PropertyMap
import net.minecraft.world.item.component.ResolvableProfile
import java.util.*


expect fun PropertyMap(): PropertyMap
expect fun ResolvableProfile(name: String?, uuid: UUID?, properties: PropertyMap): ResolvableProfile
expect val ResolvableProfile.properties: PropertyMap

expect val GameProfile.properties: PropertyMap
expect val GameProfile.name: String
expect val GameProfile.id: UUID
expect fun GameProfile.toResolvableProfile(): ResolvableProfile
