package tech.thatgravyboat.skyblockapi.platform

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.PropertyMap
import net.minecraft.world.item.component.ResolvableProfile
import net.msrandom.stub.Stub
import java.util.*


@Stub
expect fun PropertyMap(): PropertyMap
@Stub
expect fun ResolvableProfile(name: String?, uuid: UUID?, properties: PropertyMap = PropertyMap(), init: PropertyMap.() -> Unit = {}): ResolvableProfile
@Stub
expect val ResolvableProfile.properties: PropertyMap

@Stub
expect val GameProfile.properties: PropertyMap
@Stub
expect val GameProfile.name: String
@Stub
expect val GameProfile.id: UUID
@Stub
expect fun GameProfile.toResolvableProfile(): ResolvableProfile
