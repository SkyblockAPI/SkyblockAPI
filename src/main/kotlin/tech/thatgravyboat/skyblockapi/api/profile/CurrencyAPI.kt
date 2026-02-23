package tech.thatgravyboat.skyblockapi.api.profile
//? < 26.1 {
/*
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.utils.extentions.valueOfOrNull
import tech.thatgravyboat.skyblockapi.api.profile.currency.CurrencyAPI as NewCurrencyAPI

@Deprecated("Use the new CurrencyAPI")
enum class PurseType() {
    UNKNOWN,
    NORMAL,
    PIGGY,
}

@RemoveNextVersion(ReplaceWith("tech.thatgravyboat.skyblockapi.api.profile.currency.CurrencyAPI"))
object CurrencyAPI {

    val purse: Double get() = NewCurrencyAPI.purse
    val purseType: PurseType
        get() = valueOfOrNull<PurseType>(NewCurrencyAPI.purseType.name) ?: PurseType.UNKNOWN
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
*///? }
