package tech.thatgravyboat.skyblockapi.platform

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.mojang.authlib.properties.PropertyMap
import net.minecraft.world.item.component.ResolvableProfile
import java.util.UUID

public fun PropertyMap(init: Multimap<String, Property>.() -> Unit = {}): PropertyMap {
    return PropertyMap(HashMultimap.create<String, Property>().apply(init))
}

public fun ResolvableProfile(name: String = "meow", uuid: UUID = UUID.randomUUID(), init: Multimap<String, Property>.() -> Unit = {}): ResolvableProfile {
    return ResolvableProfile.createResolved(GameProfile(uuid, name, PropertyMap(init)))
}

public fun ResolvableProfile(name: String = "meow", uuid: UUID = UUID.randomUUID(), map: PropertyMap): ResolvableProfile {
    return ResolvableProfile.createResolved(GameProfile(uuid, name, map))
}
public val ResolvableProfile.properties: PropertyMap
    get() = this.partialProfile().properties

public fun GameProfile(name: String = "meow", uuid: UUID = UUID.randomUUID(), init: Multimap<String, Property>.() -> Unit = {}): GameProfile {
    return GameProfile(uuid, name, PropertyMap(init))
}

public fun GameProfile(name: String = "meow", uuid: UUID = UUID.randomUUID(), map: PropertyMap): GameProfile {
    return GameProfile(uuid, name, map)
}

public val GameProfile.properties: PropertyMap get() = this.properties
public val GameProfile.name: String get() = this.name
public val GameProfile.id: UUID get() = this.id

public fun GameProfile.toResolvableProfile(): ResolvableProfile =
    ResolvableProfile.createResolved(this)

