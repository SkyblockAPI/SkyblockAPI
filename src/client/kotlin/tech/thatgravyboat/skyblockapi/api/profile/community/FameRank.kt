package tech.thatgravyboat.skyblockapi.api.profile.community

import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.kcodec.IncludedCodec

data class FameRank(val id: String, val name: String, val multiplier: Double) {
    companion object {

        @IncludedCodec(keyable = true)
        val CODEC: Codec<FameRank> = KCodec.getCodec<String>().xmap(FameRanks::getById, FameRank::id)
    }
}

@Suppress("unused")
object FameRanks {

    private val registeredFameRanks = mutableMapOf<String, FameRank>()

    fun getByName(name: String): FameRank? = registeredFameRanks.values.find { it.name.equals(name, true) }

    fun getById(id: String): FameRank = registeredFameRanks[id] ?: NEW_PLAYER

    private fun register(key: String, name: String, multiplier: Double) =
        registeredFameRanks.getOrPut(key) { FameRank(key, name, multiplier) }

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
    val OVERSEER = register("overseer", "Overseer", 2.28)
    val REGENT = register("regent", "Regent", 2.3)
    val VICEROY = register("viceroy", "Viceroy", 2.32)
    val SOVEREIGN = register("sovereign", "Sovereign", 2.34)
    val ARCHON = register("archon", "Archon", 2.36)
    val IMPERATOR = register("imperator", "Imperator", 2.38)
    val PARAGON = register("paragon", "Paragon", 2.4)

}
