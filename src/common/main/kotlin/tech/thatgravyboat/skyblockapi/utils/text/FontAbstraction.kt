package tech.thatgravyboat.skyblockapi.utils.text

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.component.ResolvableProfile

interface FontDescriptor

data class ResourceFontDescriptor(val id: ResourceLocation) : FontDescriptor
data class AtlasSpriteFontDescriptor(val atlasId: ResourceLocation, val spriteId: ResourceLocation) : FontDescriptor
data class PlayerSpriteFontDescriptor(val profile: ResolvableProfile, val hat: Boolean) : FontDescriptor
