@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package tech.thatgravyboat.skyblockapi.utils.text

import net.minecraft.network.chat.FontDescription
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.style

internal actual fun MutableComponent.withFont(location: ResourceLocation?) =
    this.style { if (location == null) withFont(null) else withFont(FontDescription.Resource(location)) }

internal actual fun MutableComponent.font(): ResourceLocation? = (this.style.font as? FontDescription.Resource)?.id

internal actual fun MutableComponent.withFontDescriptor(location: FontDescriptor?): MutableComponent = apply {
    val description = when (location) {
        is ResourceFontDescriptor -> FontDescription.Resource(location.id)
        is PlayerSpriteFontDescriptor -> FontDescription.PlayerSprite(location.profile, location.hat)
        is AtlasSpriteFontDescriptor -> FontDescription.AtlasSprite(location.atlasId, location.spriteId)
        else -> null
    }
    this.style { withFont(description) }
}

internal actual fun MutableComponent.fontDescriptor(): FontDescriptor? = when (val font = this.style.font) {
    is FontDescription.Resource -> ResourceFontDescriptor(font.id)
    is FontDescription.PlayerSprite -> PlayerSpriteFontDescriptor(font.profile, font.hat)
    is FontDescription.AtlasSprite -> AtlasSpriteFontDescriptor(font.atlasId, font.spriteId)
    else -> null
}
