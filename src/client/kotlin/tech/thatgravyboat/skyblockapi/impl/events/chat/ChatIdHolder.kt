package tech.thatgravyboat.skyblockapi.impl.events.chat

interface ChatIdHolder {

    fun `skyblockapi$getId`(): String?

    fun `skyblockapi$setId`(id: String?)
}

