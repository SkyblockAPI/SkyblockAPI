package tech.thatgravyboat.skyblockapi.api.area.mining.events

interface MiningEvent {

    val name: String
}

data class UnknownMiningEvent(override val name: String) : MiningEvent
