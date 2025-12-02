package tech.thatgravyboat.skyblockapi.utils.text

import net.minecraft.network.chat.Component

interface ComponentLike {

    fun toComponent(): Component
}
