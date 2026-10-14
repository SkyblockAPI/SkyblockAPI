package tech.thatgravyboat.skyblockapi.utils.text

import net.minecraft.network.chat.Component

public interface ComponentLike {

    public fun toComponent(): Component
}
