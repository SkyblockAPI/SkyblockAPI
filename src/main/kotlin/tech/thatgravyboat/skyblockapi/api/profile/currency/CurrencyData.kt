package tech.thatgravyboat.skyblockapi.api.profile.currency

import me.owdding.ktcodecs.GenerateCodec

@GenerateCodec
data class CurrencyData(
    var purse: Double = 0.0,
    var purseType: PurseType = PurseType.UNKNOWN,
    var personalBank: Long = 0,
    var coopBank: Long = 0,
    var motes: Long = 0,
    var bits: Long = 0,
    var gems: Long = 0,
    var copper: Long = 0,
    var sowdust: Long = 0,
    var northStars: Long = 0,
    var soulflow: Long = 0,
)
