package tech.thatgravyboat.skyblockapi.api.profile.community

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.IncludedCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

public data class FameRank(val id: String, val name: String, val multiplier: Double) {
    companion object {

        @IncludedCodec(keyable = true)
        public val CODEC: Codec<FameRank> = SkyblockAPICodecs.getCodec<String>().xmap(FameRanks::getById, FameRank::id)
    }
}

@Suppress("unused")
public object FameRanks {

    private val registeredFameRanks = mutableMapOf<String, FameRank>()

    public fun getByName(name: String): FameRank? = registeredFameRanks.values.find { it.name.equals(name, true) }

    public fun getById(id: String): FameRank = registeredFameRanks[id] ?: NEW_PLAYER

    private fun register(key: String, name: String, multiplier: Double) =
        registeredFameRanks.getOrPut(key) { FameRank(key, name, multiplier) }

    public val NEW_PLAYER = register("new_player", "New Player", 1.0)
    public val SETTLER = register("settler", "Settler", 1.1)
    public val CITIZEN = register("citizen", "Citizen", 1.2)
    public val CONTRIBUTOR = register("contributor", "Contributor", 1.3)
    public val PHILANTHROPIST = register("philanthropist", "Philanthropist", 1.4)
    public val PATRON = register("patron", "Patron", 1.5)
    public val FAMOUS_PLAYER = register("famous_player", "Famous Player", 1.8)
    public val ATTACHE = register("attache", "Attaché", 1.9)
    public val AMBASSADOR = register("ambassador", "Ambassador", 2.0)
    public val STATESPERSON = register("statesperson", "Statesperson", 2.04)
    public val SENATOR = register("senator", "Senator", 2.08)
    public val DIGNITARY = register("dignitary", "Dignitary", 2.12)
    public val COUNCILOR = register("councilor", "Councilor", 2.16)
    public val MINISTER = register("minister", "Minister", 2.2)
    public val PREMIER = register("premier", "Premier", 2.22)
    public val CHANCELLOR = register("chancellor", "Chancellor", 2.24)
    public val SUPREME = register("supreme", "Supreme", 2.26)
    public val OVERSEER = register("overseer", "Overseer", 2.28)
    public val REGENT = register("regent", "Regent", 2.3)
    public val VICEROY = register("viceroy", "Viceroy", 2.32)
    public val SOVEREIGN = register("sovereign", "Sovereign", 2.34)
    public val ARCHON = register("archon", "Archon", 2.36)
    public val IMPERATOR = register("imperator", "Imperator", 2.38)
    public val PARAGON = register("paragon", "Paragon", 2.4)

}
