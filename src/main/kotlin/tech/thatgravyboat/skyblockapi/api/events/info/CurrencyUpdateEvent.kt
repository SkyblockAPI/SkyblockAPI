package tech.thatgravyboat.skyblockapi.api.events.info

import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

public sealed class CurrencyUpdateEvent<N : Number>(public val new: N, public val old: N) : SkyBlockEvent() {

    public class Purse(new: Double, old: Double) : CurrencyUpdateEvent<Double>(new, old)
    public class Bank(new: Long, old: Long) : CurrencyUpdateEvent<Long>(new, old)
    public class CoopBank(new: Long, old: Long) : CurrencyUpdateEvent<Long>(new, old)
    public class Bits(new: Long, old: Long) : CurrencyUpdateEvent<Long>(new, old)
    public class Motes(new: Long, old: Long) : CurrencyUpdateEvent<Long>(new, old)
    public class Copper(new: Long, old: Long) : CurrencyUpdateEvent<Long>(new, old)
    public class SowDust(new: Long, old: Long) : CurrencyUpdateEvent<Long>(new, old)
    public class Kernels(new: Long, old: Long) : CurrencyUpdateEvent<Long>(new, old)
    public class NorthStars(new: Long, old: Long) : CurrencyUpdateEvent<Long>(new, old)
    public class Gems(new: Long, old: Long) : CurrencyUpdateEvent<Long>(new, old)

    public companion object {
        @get:JvmName("diffLong")
        public val CurrencyUpdateEvent<Long>.diff: Long get() = new - old

        @get:JvmName("diffDouble")
        public val CurrencyUpdateEvent<Double>.diff: Double get() = new - old

    }
}
