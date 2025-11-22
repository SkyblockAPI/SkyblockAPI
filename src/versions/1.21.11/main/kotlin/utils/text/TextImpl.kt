@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package tech.thatgravyboat.skyblockapi.utils.text

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FontDescription
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.Identifier
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.style

internal actual fun MutableComponent.withFont(location: Identifier?) =
    this.style { if (location == null) withFont(null) else withFont(FontDescription.Resource(location)) }

internal actual fun Component.font(): Identifier? = (this.style.font as? FontDescription.Resource)?.id
