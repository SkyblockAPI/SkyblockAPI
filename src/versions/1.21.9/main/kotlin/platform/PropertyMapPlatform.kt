@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package tech.thatgravyboat.skyblockapi.platform

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.mojang.authlib.properties.PropertyMap
import net.minecraft.world.item.component.ResolvableProfile
import java.util.*

actual fun PropertyMap(init: Multimap<String, Property>.() -> Unit): PropertyMap = PropertyMap(HashMultimap.create<String, Property>().apply(init))
actual fun ResolvableProfile(name: String, uuid: UUID, init: Multimap<String, Property>.() -> Unit): ResolvableProfile {
    return ResolvableProfile.createResolved(GameProfile(uuid, name, PropertyMap(init)))
}

actual fun ResolvableProfile(name: String, uuid: UUID, map: PropertyMap): ResolvableProfile {
    return ResolvableProfile.createResolved(GameProfile(uuid, name, map))
}


actual val ResolvableProfile.properties: PropertyMap get() = this.partialProfile().properties

actual fun GameProfile(name: String, uuid: UUID, init: Multimap<String, Property>.() -> Unit): GameProfile = GameProfile(uuid, name, PropertyMap(init))
actual fun GameProfile(name: String, uuid: UUID, map: PropertyMap): GameProfile = GameProfile(uuid, name, map)
actual val GameProfile.properties: PropertyMap get() = this.properties()
actual val GameProfile.name: String get() = this.name()
actual val GameProfile.id: UUID get() = this.id()
actual fun GameProfile.toResolvableProfile(): ResolvableProfile = ResolvableProfile.createResolved(this)
