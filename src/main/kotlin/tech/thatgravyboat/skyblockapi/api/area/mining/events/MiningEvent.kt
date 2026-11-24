package tech.thatgravyboat.skyblockapi.api.area.mining.events

public interface MiningEvent {

    public val name: String
}

public data class UnknownMiningEvent(override val name: String) : MiningEvent
