@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package tech.thatgravyboat.skyblockapi.utils.text

import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.style

internal actual fun MutableComponent.withFont(location: ResourceLocation?) = this.style { withFont(location) }
internal actual fun MutableComponent.font(): ResourceLocation? = this.style.font

internal actual fun MutableComponent.withFontDescriptor(location: FontDescriptor?): MutableComponent =
    apply { let { this.style.withFont((location as? ResourceFontDescriptor)?.id) } }

internal actual fun MutableComponent.fontDescriptor(): FontDescriptor? = this.style.font?.let { ResourceFontDescriptor(it) }
