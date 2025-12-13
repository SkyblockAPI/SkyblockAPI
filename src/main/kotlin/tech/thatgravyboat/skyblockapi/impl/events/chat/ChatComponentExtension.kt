package tech.thatgravyboat.skyblockapi.impl.events.chat

import net.minecraft.client.gui.components.ChatComponent

interface ChatComponentExtension {

    fun `skyblockapi$setIdForMessage`(id: String?)
}

fun ChatComponent.setMessageId(id: String, block: () -> Unit) {
    (this as ChatComponentExtension).`skyblockapi$setIdForMessage`(id)
    block()
    (this as ChatComponentExtension).`skyblockapi$setIdForMessage`(null)
}
