package tech.thatgravyboat.skyblockapi.api.profile

import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.profile.currency.CurrencyAPI as NewCurrencyAPI
import tech.thatgravyboat.skyblockapi.api.profile.currency.PurseType as NewPurseType

@Deprecated("Use the new CurrencyAPI")
enum class PurseType(internal val newType: NewPurseType) {
    UNKNOWN(NewPurseType.UNKNOWN),
    NORMAL(NewPurseType.NORMAL),
    PIGGY(NewPurseType.PIGGY),
}

@RemoveNextVersion(ReplaceWith("tech.thatgravyboat.skyblockapi.api.profile.currency.CurrencyAPI"))
object CurrencyAPI {

    val purse: Double get() = NewCurrencyAPI.purse

    val purseType: PurseType
        get() = NewCurrencyAPI.purseType.let { newType -> PurseType.entries.find { it.newType == newType } ?: PurseType.UNKNOWN }

    val personalBank: Long get() = NewCurrencyAPI.personalBank

    val coopBank: Long get() = NewCurrencyAPI.coopBank

    val bank get() = NewCurrencyAPI.bank

    val motes: Long get() = NewCurrencyAPI.motes

    val bits: Long get() = NewCurrencyAPI.bits

    val gems: Long get() = NewCurrencyAPI.gems

    val copper: Long get() = NewCurrencyAPI.copper

    val northStars: Long get() = NewCurrencyAPI.northStars

    val soulflow: Long get() = NewCurrencyAPI.soulflow
}
