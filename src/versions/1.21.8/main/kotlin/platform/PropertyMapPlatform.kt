@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package tech.thatgravyboat.skyblockapi.platform

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.PropertyMap
import net.minecraft.world.item.component.ResolvableProfile
import java.util.*

actual fun PropertyMap(): PropertyMap = PropertyMap()
actual fun ResolvableProfile(name: String?, uuid: UUID?, properties: PropertyMap, init: PropertyMap.() -> Unit): ResolvableProfile {
    return ResolvableProfile(Optional.ofNullable(name), Optional.ofNullable(uuid), properties.apply(init))
}

actual val ResolvableProfile.properties: PropertyMap get() = this.properties()

actual val GameProfile.properties: PropertyMap get() = this.properties
actual val GameProfile.name: String get() = this.name
actual val GameProfile.id: UUID get() = this.id
actual fun GameProfile.toResolvableProfile(): ResolvableProfile = ResolvableProfile(this)
