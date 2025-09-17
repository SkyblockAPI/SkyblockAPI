@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package tech.thatgravyboat.skyblockapi.platform

import com.mojang.authlib.properties.PropertyMap
import net.minecraft.world.item.component.ResolvableProfile
import java.util.*

actual fun PropertyMap(): PropertyMap = PropertyMap()
actual fun ResolvableProfile(name: String?, uuid: UUID?, properties: PropertyMap): ResolvableProfile =
    ResolvableProfile(Optional.ofNullable(name), Optional.ofNullable(uuid), properties)

