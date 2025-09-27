package tech.thatgravyboat.skyblockapi.platform

import com.google.common.collect.Multimap
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.mojang.authlib.properties.PropertyMap
import net.minecraft.world.item.component.ResolvableProfile
import net.msrandom.stub.Stub
import java.util.*


@Stub
expect fun PropertyMap(init: Multimap<String, Property>.() -> Unit = {}): PropertyMap

@Stub
expect fun ResolvableProfile(name: String = "meow", uuid: UUID = UUID.randomUUID(), init: Multimap<String, Property>.() -> Unit = {}): ResolvableProfile
@Stub
expect fun ResolvableProfile(name: String = "meow", uuid: UUID = UUID.randomUUID(), map: PropertyMap): ResolvableProfile
@Stub
expect val ResolvableProfile.properties: PropertyMap

@Stub
expect fun GameProfile(name: String = "meow", uuid: UUID = UUID.randomUUID(), init: Multimap<String, Property>.() -> Unit = {}): GameProfile

@Stub
expect fun GameProfile(name: String = "meow", uuid: UUID = UUID.randomUUID(), map: PropertyMap): GameProfile
@Stub
expect val GameProfile.properties: PropertyMap
@Stub
expect val GameProfile.name: String
@Stub
expect val GameProfile.id: UUID
@Stub
expect fun GameProfile.toResolvableProfile(): ResolvableProfile
