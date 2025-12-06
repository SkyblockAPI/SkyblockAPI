package tech.thatgravyboat.skyblockapi.api.events.info

import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

sealed class CurrencyUpdateEvent<N : Number>(val new: N, val old: N) : SkyBlockEvent() {

    class Purse(new: Double, old: Double) : CurrencyUpdateEvent<Double>(new, old)
    class Bank(new: Long, old: Long) : CurrencyUpdateEvent<Long>(new, old)
    class CoopBank(new: Long, old: Long) : CurrencyUpdateEvent<Long>(new, old)
    class Bits(new: Long, old: Long) : CurrencyUpdateEvent<Long>(new, old)
    class Motes(new: Long, old: Long) : CurrencyUpdateEvent<Long>(new, old)
    class Copper(new: Long, old: Long) : CurrencyUpdateEvent<Long>(new, old)
    class SowDust(new: Long, old: Long) : CurrencyUpdateEvent<Long>(new, old)
    class NorthStars(new: Long, old: Long) : CurrencyUpdateEvent<Long>(new, old)
    class Gems(new: Long, old: Long) : CurrencyUpdateEvent<Long>(new, old)

    companion object {
        @get:JvmName("diffLong")
        val CurrencyUpdateEvent<Long>.diff get() = new - old

        @get:JvmName("diffDouble")
        val CurrencyUpdateEvent<Double>.diff get() = new - old

    }
}
