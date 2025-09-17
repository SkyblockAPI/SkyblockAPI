@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package tech.thatgravyboat.skyblockapi.platform

import com.google.common.collect.LinkedHashMultimap
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.PropertyMap
import net.minecraft.world.item.component.ResolvableProfile
import java.util.*

actual fun PropertyMap(): PropertyMap = PropertyMap(LinkedHashMultimap.create())
actual fun ResolvableProfile(name: String?, uuid: UUID?, properties: PropertyMap): ResolvableProfile =
    ResolvableProfile.createResolved(GameProfile(uuid, name, properties))

actual val ResolvableProfile.properties: PropertyMap get() = this.partialProfile().properties

actual val GameProfile.properties: PropertyMap get() = this.properties()
actual val GameProfile.name: String get() = this.name()
actual val GameProfile.id: UUID get() = this.id()
actual fun GameProfile.toResolvableProfile(): ResolvableProfile = ResolvableProfile.createResolved(this)
