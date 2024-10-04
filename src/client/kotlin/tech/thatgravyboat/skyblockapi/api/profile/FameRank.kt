package tech.thatgravyboat.skyblockapi.api.profile

import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec

@GenerateCodec
data class FameRank(val name: String, val multiplier: Double)

@Suppress("unused")
object FameRanks {

    val registeredFameRanks = mutableMapOf<String, FameRank>()

    private fun register(key: String, name: String, multiplier: Double) =
        registeredFameRanks.computeIfAbsent(key) { FameRank(name, multiplier) }

    val NEW_PLAYER = register("new_player", "New Player", 1.0)
    val SETTLER = register("settler", "Settler", 1.1)
    val CITIZEN = register("citizen", "Citizen", 1.2)
    val CONTRIBUTOR = register("contributor", "Contributor", 1.3)
    val PHILANTHROPIST = register("philanthropist", "Philanthropist", 1.4)
    val PATRON = register("patron", "Patron", 1.5)
    val FAMOUS_PLAYER = register("famous_player", "Famous Player", 1.8)
    val ATTACHE = register("attache", "Attaché", 1.9)
    val AMBASSADOR = register("ambassador", "Ambassador", 2.0)
    val STATESPERSON = register("statesperson", "Statesperson", 2.04)
    val SENATOR = register("senator", "Senator", 2.08)
    val DIGNITARY = register("dignitary", "Dignitary", 2.12)
    val COUNCILOR = register("councilor", "Councilor", 2.16)
    val MINISTER = register("minister", "Minister", 2.2)
    val PREMIER = register("premier", "Premier", 2.22)
    val CHANCELLOR = register("chancellor", "Chancellor", 2.24)
    val SUPREME = register("supreme", "Supreme", 2.26)

}
