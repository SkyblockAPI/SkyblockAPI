package tech.thatgravyboat.skyblockapi.platform

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.mojang.authlib.properties.PropertyMap
import net.minecraft.world.item.component.ResolvableProfile
import java.util.UUID


fun PropertyMap(init: Multimap<String, Property>.() -> Unit = {}): PropertyMap {
    //? if > 1.21.8 {
    return PropertyMap(HashMultimap.create<String, Property>().apply(init))
    //?} else
    /*return PropertyMap().apply(init)*/
}

fun ResolvableProfile(name: String = "meow", uuid: UUID = UUID.randomUUID(), init: Multimap<String, Property>.() -> Unit = {}): ResolvableProfile {
    //? if > 1.21.8 {
    return ResolvableProfile.createResolved(GameProfile(uuid, name, PropertyMap(init)))
    //?} else
    /*return ResolvableProfile(Optional.of(name), Optional.of(uuid), PropertyMap(init))*/
}
fun ResolvableProfile(name: String = "meow", uuid: UUID = UUID.randomUUID(), map: PropertyMap): ResolvableProfile {
    //? if > 1.21.8 {
    return ResolvableProfile.createResolved(GameProfile(uuid, name, map))
    //?} else
    /*return ResolvableProfile(Optional.of(name), Optional.of(uuid), map)*/
}
val ResolvableProfile.properties: PropertyMap
    //? if > 1.21.8 {
    get() = this.partialProfile().properties
    //?} else
    /*get() = this.properties()*/

fun GameProfile(name: String = "meow", uuid: UUID = UUID.randomUUID(), init: Multimap<String, Property>.() -> Unit = {}): GameProfile {
    //? if > 1.21.8 {
    return GameProfile(uuid, name, PropertyMap(init))
    //?} else {
    /*return GameProfile(uuid, name).apply {
        this.properties.apply(init)
    }
    *///?}
}

fun GameProfile(name: String = "meow", uuid: UUID = UUID.randomUUID(), map: PropertyMap): GameProfile {
    //? if > 1.21.8 {
    return GameProfile(uuid, name, map)
    //?} else {
    /*return GameProfile(uuid, name).apply {
        this.properties.putAll(map)
    }
    *///?}
}

val GameProfile.properties: PropertyMap get() = this.properties
val GameProfile.name: String get() = this.name
val GameProfile.id: UUID get() = this.id

fun GameProfile.toResolvableProfile(): ResolvableProfile =
    //? if > 1.21.8 {
    ResolvableProfile.createResolved(this)
    //? } else
    //ResolvableProfile(this)

