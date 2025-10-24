@file:Suppress("ACTUAL_WITHOUT_EXPECT")

package tech.thatgravyboat.skyblockapi.utils.text

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.style

internal actual fun MutableComponent.withFont(location: ResourceLocation?) = this.style { withFont(location) }
internal actual fun Component.font(): ResourceLocation? = this.style.font
