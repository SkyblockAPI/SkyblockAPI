package tech.thatgravyboat.skyblockapi.api.profile.community

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.IncludedCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

public data class FameRank(val id: String, val name: String, val multiplier: Double) {
    public companion object {

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

    public val NEW_PLAYER: FameRank = register("new_player", "New Player", 1.0)
    public val SETTLER: FameRank = register("settler", "Settler", 1.1)
    public val CITIZEN: FameRank = register("citizen", "Citizen", 1.2)
    public val CONTRIBUTOR: FameRank = register("contributor", "Contributor", 1.3)
    public val PHILANTHROPIST: FameRank = register("philanthropist", "Philanthropist", 1.4)
    public val PATRON: FameRank = register("patron", "Patron", 1.5)
    public val FAMOUS_PLAYER: FameRank = register("famous_player", "Famous Player", 1.8)
    public val ATTACHE: FameRank = register("attache", "Attaché", 1.9)
    public val AMBASSADOR: FameRank = register("ambassador", "Ambassador", 2.0)
    public val STATESPERSON: FameRank = register("statesperson", "Statesperson", 2.04)
    public val SENATOR: FameRank = register("senator", "Senator", 2.08)
    public val DIGNITARY: FameRank = register("dignitary", "Dignitary", 2.12)
    public val COUNCILOR: FameRank = register("councilor", "Councilor", 2.16)
    public val MINISTER: FameRank = register("minister", "Minister", 2.2)
    public val PREMIER: FameRank = register("premier", "Premier", 2.22)
    public val CHANCELLOR: FameRank = register("chancellor", "Chancellor", 2.24)
    public val SUPREME: FameRank = register("supreme", "Supreme", 2.26)
    public val OVERSEER: FameRank = register("overseer", "Overseer", 2.28)
    public val REGENT: FameRank = register("regent", "Regent", 2.3)
    public val VICEROY: FameRank = register("viceroy", "Viceroy", 2.32)
    public val SOVEREIGN: FameRank = register("sovereign", "Sovereign", 2.34)
    public val ARCHON: FameRank = register("archon", "Archon", 2.36)
    public val IMPERATOR: FameRank = register("imperator", "Imperator", 2.38)
    public val PARAGON: FameRank = register("paragon", "Paragon", 2.4)

}
