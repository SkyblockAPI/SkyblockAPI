@file:Suppress("unused")

package tech.thatgravyboat.skyblockapi.api.data

import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.area.hub.ElectionAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.toScreamingSnakeCase

@ConsistentCopyVisibility
data class MayorCandidate internal constructor(
    val id: String,
    val candidateName: String,
    val perks: MutableList<MayorPerk>
) {
    internal constructor(id: String, candidateName: String, vararg perks: MayorPerk) : this(id, candidateName, perks.toMutableList())
}

object MayorCandidates {
    private val _mayors = mutableMapOf<String, MayorCandidate>()
    val mayors: Map<String, MayorCandidate> by _mayors
    fun register(candidateName: String, vararg perks: MayorPerk, id: String = candidateName.toScreamingSnakeCase()): MayorCandidate {
        return _mayors.getOrPut(id) { MayorCandidate(id, candidateName, *perks) }
    }

    val AATROX = register("Aatrox", MayorPerks.SLASHED_PRICING, MayorPerks.SLAYER_XP_BUFF, MayorPerks.PATHFINDER)
    val COLE = register("Cole", MayorPerks.PROSPECTION, MayorPerks.MINING_XP_BUFF, MayorPerks.MINING_FIESTA, MayorPerks.MOLTEN_FORGE)
    val DIANA = register("Diana", MayorPerks.LUCKY, MayorPerks.MYTHOLOGICAL_RITUAL, MayorPerks.PET_XP_BUFF, MayorPerks.SHARING_IS_CARING)
    val DIAZ = register("Diaz", MayorPerks.SHOPPING_SPREE, MayorPerks.VOLUME_TRADING, MayorPerks.STOCK_EXCHANGE, MayorPerks.LONG_TERM_INVESTMENT)
    val FINNEGAN = register("Finnegan", MayorPerks.PELT_POCALYPSE, MayorPerks.GOATED, MayorPerks.BLOOMING_BUSINESS, MayorPerks.PEST_ERADICATOR)
    val FOXY = register("Foxy", MayorPerks.SWEET_BENEVOLENCE, MayorPerks.A_TIME_FOR_GIVING, MayorPerks.CHIVALROUS_CARNIVAL, MayorPerks.EXTRA_EVENT)
    val MARINA = register("Marina", MayorPerks.FISHING_XP_BUFF, MayorPerks.LUCK_OF_THE_SEA, MayorPerks.FISHING_FESTIVAL, MayorPerks.DOUBLE_TROUBLE)
    val PAUL = register("Paul", MayorPerks.MARAUDER, MayorPerks.EZPZ, MayorPerks.BENEDICTION)
    val SCORPIUS = register("Scorpius", MayorPerks.BRIBE, MayorPerks.DARKER_AUCTIONS)
    val JERRY = register("Jerry", MayorPerks.PERKPOCALYPSE, MayorPerks.STATSPOCALYPSE, MayorPerks.JERRYPOCALYPSE)
    val DERPY = register("Derpy", MayorPerks.TURBO_MINIONS, MayorPerks.QUAD_TAXES, MayorPerks.DOUBLE_MOBS_HP, MayorPerks.MOAR_SKILLZ)

}

@RemoveNextVersion(ReplaceWith("MayorCandidate"), DeprecationLevel.ERROR)
enum class Candidate(val candidateName: String, vararg val perks: Perk) {
    AATROX("Aatrox", Perk.SLASHED_PRICING, Perk.SLAYER_XP_BUFF, Perk.PATHFINDER),
    COLE("Cole", Perk.PROSPECTION, Perk.MINING_XP_BUFF, Perk.MINING_FIESTA, Perk.MOLTEN_FORGE),
    DIANA("Diana", Perk.LUCKY, Perk.MYTHOLOGICAL_RITUAL, Perk.PET_XP_BUFF, Perk.SHARING_IS_CARING),
    DIAZ("Diaz", Perk.SHOPPING_SPREE, Perk.VOLUME_TRADING, Perk.STOCK_EXCHANGE, Perk.LONG_TERM_INVESTMENT),
    FINNEGAN("Finnegan", Perk.PELT_POCALYPSE, Perk.GOATED, Perk.BLOOMING_BUSINESS, Perk.PEST_ERADICATOR),
    FOXY("Foxy", Perk.SWEET_BENEVOLENCE, Perk.A_TIME_FOR_GIVING, Perk.CHIVALROUS_CARNIVAL, Perk.EXTRA_EVENT),
    MARINA("Marina", Perk.FISHING_XP_BUFF, Perk.LUCK_OF_THE_SEA, Perk.FISHING_FESTIVAL, Perk.DOUBLE_TROUBLE),
    PAUL("Paul", Perk.MARAUDER, Perk.EZPZ, Perk.BENEDICTION),
    SCORPIUS("Scorpius", Perk.BRIBE, Perk.DARKER_AUCTIONS),
    JERRY("Jerry", Perk.PERKPOCALYPSE, Perk.STATSPOCALYPSE, Perk.JERRYPOCALYPSE),
    DERPY("Derpy", Perk.TURBO_MINIONS, Perk.QUAD_TAXES, Perk.DOUBLE_MOBS_HP, Perk.MOAR_SKILLZ),
    UNKNOWN("Unknown", Perk.SLASHED_PRICING, Perk.SLAYER_XP_BUFF, Perk.PATHFINDER),
    ;

    val activePerks get() = perks.filter { it.active }
    val isActive get() = this == ElectionAPI.currentMayor || this == ElectionAPI.currentMinister
    val isSpecial by lazy { this in setOf(SCORPIUS, JERRY, DERPY) }

    internal fun addAllPerks(): Candidate = apply { perks.forEach { it.active = true } }
    internal fun clearAllPerks(): Candidate = apply { perks.forEach { it.active = false } }
    override fun toString() = candidateName

    companion object {
        fun getCandidate(candidateName: String): Candidate? = entries.find { it.candidateName == candidateName }
    }
}

@ConsistentCopyVisibility
data class MayorPerk internal constructor(
    val id: String,
    val perkName: String,
) {
    var active: Boolean = false
        internal set
    var description: String = "Not available"
}

@Suppress("unused")
object MayorPerks {
    private val _perks = mutableMapOf<String, MayorPerk>()
    val perks: Map<String, MayorPerk> by _perks

    fun register(perkName: String, id: String = perkName.toScreamingSnakeCase()): MayorPerk {
        return _perks.getOrPut(id) { MayorPerk(id, perkName) }
    }

    fun reset() = perks.values.forEach { it.active = false }
    fun getPerk(perkName: String) = perks.values.find { it.perkName == perkName }


    val SLASHED_PRICING = register("SLASHED Pricing")
    val SLAYER_XP_BUFF = register("Slayer XP Buff")
    val PATHFINDER = register("Pathfinder")

    val PROSPECTION = register("Prospection")
    val MINING_XP_BUFF = register("Mining XP Buff")
    val MINING_FIESTA = register("Mining Fiesta")
    val MOLTEN_FORGE = register("Molten Forge")

    val LUCKY = register("Lucky!")
    val MYTHOLOGICAL_RITUAL = register("Mythological Ritual")
    val PET_XP_BUFF = register("Pet XP Buff")
    val SHARING_IS_CARING = register("Sharing is Caring")

    val SHOPPING_SPREE = register("Shopping Spree")
    val VOLUME_TRADING = register("Volume Trading")
    val STOCK_EXCHANGE = register("Stock Exchange")
    val LONG_TERM_INVESTMENT = register("Long Term Investment")

    val PELT_POCALYPSE = register("PELT_POCALYPSE","Pelt-pocalypse")
    val GOATED = register("GOATED","GOATed")
    val BLOOMING_BUSINESS = register("BLOOMING_BUSINESS","Blooming Business")
    val PEST_ERADICATOR = register("PEST_ERADICATOR","Pest Eradicator")

    val SWEET_BENEVOLENCE = register("SWEET_BENEVOLENCE","Sweet Benevolence")
    val A_TIME_FOR_GIVING = register("A_TIME_FOR_GIVING","A Time for Giving")
    val CHIVALROUS_CARNIVAL = register("CHIVALROUS_CARNIVAL","Chivalrous Carnival")
    val EXTRA_EVENT = register("EXTRA_EVENT","Extra Event")

    val FISHING_XP_BUFF = register("FISHING_XP_BUFF","Fishing XP Buff")
    val LUCK_OF_THE_SEA = register("LUCK_OF_THE_SEA","Luck of the Sea 2.0")
    val FISHING_FESTIVAL = register("FISHING_FESTIVAL","Fishing Festival")
    val DOUBLE_TROUBLE = register("DOUBLE_TROUBLE","Double Trouble")

    val MARAUDER = register("MARAUDER","Marauder")
    val EZPZ = register("EZPZ","EZPZ")
    val BENEDICTION = register("BENEDICTION","Benediction")

    val BRIBE = register("BRIBE","Bribe")
    val DARKER_AUCTIONS = register("DARKER_AUCTIONS","Darker Auctions")

    val PERKPOCALYPSE = register("PERKPOCALYPSE","Perkpocalypse")
    val STATSPOCALYPSE = register("STATSPOCALYPSE","Statspocalypse")
    val JERRYPOCALYPSE = register("JERRYPOCALYPSE","Jerrypocalypse")

    val TURBO_MINIONS = register("TURBO_MINIONS","TURBO MINIONS!!!")
    val QUAD_TAXES = register("QUAD_TAXES","QUAD TAXES!!!")
    val DOUBLE_MOBS_HP = register("DOUBLE_MOBS_HP","DOUBLE MOBS HP!!!")
    val MOAR_SKILLZ = register("MOAR_SKILLZ","MOAR SKILLZ!!!")
}

@RemoveNextVersion
enum class Perk(val perkName: String) {
    // Aatrox
    SLASHED_PRICING("SLASHED Pricing"),
    SLAYER_XP_BUFF("Slayer XP Buff"),
    PATHFINDER("Pathfinder"),

    // Cole
    PROSPECTION("Prospection"),
    MINING_XP_BUFF("Mining XP Buff"),
    MINING_FIESTA("Mining Fiesta"),
    MOLTEN_FORGE("Molten Forge"),

    // Diana
    LUCKY("Lucky!"),
    MYTHOLOGICAL_RITUAL("Mythological Ritual"),
    PET_XP_BUFF("Pet XP Buff"),
    SHARING_IS_CARING("Sharing is Caring"),

    // Diaz
    SHOPPING_SPREE("Shopping Spree"),
    VOLUME_TRADING("Volume Trading"),
    STOCK_EXCHANGE("Stock Exchange"),
    LONG_TERM_INVESTMENT("Long Term Investment"),

    // Finnegan
    PELT_POCALYPSE("Pelt-pocalypse"),
    GOATED("GOATed"),
    BLOOMING_BUSINESS("Blooming Business"),
    PEST_ERADICATOR("Pest Eradicator"),

    // Foxy
    SWEET_BENEVOLENCE("Sweet Benevolence"),
    A_TIME_FOR_GIVING("A Time for Giving"),
    CHIVALROUS_CARNIVAL("Chivalrous Carnival"),
    EXTRA_EVENT("Extra Event"),

    // Marina
    FISHING_XP_BUFF("Fishing XP Buff"),
    LUCK_OF_THE_SEA("Luck of the Sea 2.0"),
    FISHING_FESTIVAL("Fishing Festival"),
    DOUBLE_TROUBLE("Double Trouble"),

    // Paul
    MARAUDER("Marauder"),
    EZPZ("EZPZ"),
    BENEDICTION("Benediction"),

    // Scorpius
    BRIBE("Bribe"),
    DARKER_AUCTIONS("Darker Auctions"),

    // Jerry
    PERKPOCALYPSE("Perkpocalypse"),
    STATSPOCALYPSE("Statspocalypse"),
    JERRYPOCALYPSE("Jerrypocalypse"),

    // Derpy
    TURBO_MINIONS("TURBO MINIONS!!!"),
    QUAD_TAXES("QUAD TAXES!!!"),
    DOUBLE_MOBS_HP("DOUBLE MOBS HP!!!"),
    MOAR_SKILLZ("MOAR SKILLZ!!!"),
    ;

    var active = false
        internal set
    var description = "Not available"

    companion object {
        fun reset() {
            entries.forEach { it.active = false }
        }

        fun getPerk(perkName: String): Perk? = entries.find { it.perkName == perkName }
    }
}
