package tech.thatgravyboat.skyblockapi.api.data

import net.minecraft.util.TriState
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.area.hub.ElectionAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.isInFuture
import tech.thatgravyboat.skyblockapi.utils.extentions.toScreamingSnakeCase

@ConsistentCopyVisibility
data class MayorCandidate internal constructor(
    val id: String,
    val candidateName: String,
    val perks: MutableSet<MayorPerk>,
    val isSpecial: Boolean,
) {
    val activePerks: Collection<MayorPerk> get() = perks.filter { it.active }
    val isActive: Boolean get() {
        if (ElectionAPI.mayor == this || ElectionAPI.minister == this) return true
        val (jerryCandidate, time) = ElectionAPI.currentJerryCandidate ?: return false
        return jerryCandidate == this && time.isInFuture()
    }

    internal fun addAllPerks(): MayorCandidate = apply { perks.forEach { it.active = true } }
    internal fun clearAllPerks(): MayorCandidate = apply { perks.forEach { it.active = false } }
    override fun toString(): String = candidateName
}

object MayorCandidates {
    private val _mayors = mutableMapOf<String, MayorCandidate>()
    val mayors: Collection<MayorCandidate> by _mayors::values
    internal val mayorsMap: Map<String, MayorCandidate> get() = _mayors

    //region Candidates
    val AATROX = register("Aatrox", MayorPerks.SLASHED_PRICING, MayorPerks.SLAYER_XP_BUFF, MayorPerks.PATHFINDER)
    val COLE = register("Cole", MayorPerks.PROSPECTION, MayorPerks.MINING_XP_BUFF, MayorPerks.MINING_FIESTA, MayorPerks.MOLTEN_FORGE)
    val DIANA = register("Diana", MayorPerks.HUNTRESS_INTUITION, MayorPerks.MYTHOLOGICAL_RITUAL, MayorPerks.PET_XP_BUFF, MayorPerks.SHARING_IS_CARING)
    val DIAZ = register("Diaz", MayorPerks.SHOPPING_SPREE, MayorPerks.VOLUME_TRADING, MayorPerks.STOCK_EXCHANGE, MayorPerks.LONG_TERM_INVESTMENT)
    val FINNEGAN = register("Finnegan", MayorPerks.PELT_POCALYPSE, MayorPerks.GOATED, MayorPerks.BLOOMING_BUSINESS, MayorPerks.PEST_ERADICATOR)
    val FOXY = register("Foxy", MayorPerks.SWEET_BENEVOLENCE, MayorPerks.A_TIME_FOR_GIVING, MayorPerks.CHIVALROUS_CARNIVAL, MayorPerks.EXTRA_EVENT)
    val MARINA = register("Marina", MayorPerks.FISHING_XP_BUFF, MayorPerks.LUCK_OF_THE_SEA, MayorPerks.FISHING_FESTIVAL, MayorPerks.DOUBLE_TROUBLE)
    val PAUL = register("Paul", MayorPerks.MARAUDER, MayorPerks.EZPZ, MayorPerks.BENEDICTION)

    // Special Mayors
    val SCORPIUS = register("Scorpius", MayorPerks.BRIBE, MayorPerks.DARKER_AUCTIONS, isSpecial = true)
    val JERRY = register("Jerry", MayorPerks.PERKPOCALYPSE, MayorPerks.STATSPOCALYPSE, MayorPerks.JERRYPOCALYPSE, isSpecial = true)
    val DERPY = register("Derpy", MayorPerks.TURBO_MINIONS, MayorPerks.QUAD_TAXES, MayorPerks.DOUBLE_MOBS_HP, MayorPerks.MOAR_SKILLZ, isSpecial = true)
    val AURA = register("Aura", MayorPerks.FUNDRAISING, MayorPerks.MINION_UNION, MayorPerks.UNIVERSAL_INCOME, MayorPerks.WORK_BETTER, MayorPerks.WORK_HARDER, MayorPerks.WORK_SMARTER, isSpecial = true)
    //endregion

    fun getCandidateById(id: String): MayorCandidate? = _mayors[id]
    fun getCandidate(candidateName: String): MayorCandidate? = mayors.find { it.candidateName == candidateName }

    internal fun register(candidateName: String, vararg perks: MayorPerk, id: String = candidateName.toScreamingSnakeCase(), isSpecial: Boolean = false): MayorCandidate {
        return _mayors.getOrPut(id) { MayorCandidate(id, candidateName, perks.toMutableSet(), isSpecial) }
    }
}

@ConsistentCopyVisibility
data class MayorPerk internal constructor(
    val id: String,
    val perkName: String,
    var description: String = "Not available",
) {
    internal var overrideState: TriState = DEFAULT
    var active: Boolean = false
        get() = overrideState.toBoolean(field)
        internal set
}


@Suppress("unused")
object MayorPerks {
    private val _perks = mutableMapOf<String, MayorPerk>()
    val perks: Collection<MayorPerk> by _perks::values
    internal val perksMap: Map<String, MayorPerk> get() = _perks

    //region Perks
    // Aatrox
    val SLASHED_PRICING = register("SLASHED Pricing")
    val SLAYER_XP_BUFF = register("Slayer XP Buff")
    val PATHFINDER = register("Pathfinder")

    // Cole
    val PROSPECTION = register("Prospection")
    val MINING_XP_BUFF = register("Mining XP Buff")
    val MINING_FIESTA = register("Mining Fiesta")
    val MOLTEN_FORGE = register("Molten Forge")

    // Diana
    @RemoveNextVersion
    val LUCKY = register("Lucky!")
    val HUNTRESS_INTUITION = register("Huntress' Intuition")
    val MYTHOLOGICAL_RITUAL = register("Mythological Ritual")
    val PET_XP_BUFF = register("Pet XP Buff")
    val SHARING_IS_CARING = register("Sharing is Caring")

    // Diaz
    val SHOPPING_SPREE = register("Shopping Spree")
    val VOLUME_TRADING = register("Volume Trading")
    val STOCK_EXCHANGE = register("Stock Exchange")
    val LONG_TERM_INVESTMENT = register("Long Term Investment")

    // Finnegan
    val PELT_POCALYPSE = register("Pelt-pocalypse")
    val GOATED = register("GOATed", id = "GOATED")
    val BLOOMING_BUSINESS = register("Blooming Business")
    val PEST_ERADICATOR = register("Pest Eradicator")

    // Foxy
    val SWEET_BENEVOLENCE = register("Sweet Benevolence")
    val A_TIME_FOR_GIVING = register("A Time for Giving")
    val CHIVALROUS_CARNIVAL = register("Chivalrous Carnival")
    val EXTRA_EVENT = register("Extra Event")

    // Marina
    val FISHING_XP_BUFF = register("Fishing XP Buff")
    val LUCK_OF_THE_SEA = register("Luck of the Sea 2.0", id = "LUCK_OF_THE_SEA")
    val FISHING_FESTIVAL = register("Fishing Festival")
    val DOUBLE_TROUBLE = register("Double Trouble")

    // Paul
    val MARAUDER = register("Marauder")
    val EZPZ = register("EZPZ")
    val BENEDICTION = register("Benediction")

    // Scorpius
    val BRIBE = register("Bribe")
    val DARKER_AUCTIONS = register("Darker Auctions")

    // Jerry
    val PERKPOCALYPSE = register("Perkpocalypse")
    val STATSPOCALYPSE = register("Statspocalypse")
    val JERRYPOCALYPSE = register("Jerrypocalypse")

    // Derpy
    val TURBO_MINIONS = register("TURBO MINIONS!!!", id = "TURBO_MINIONS",)
    val QUAD_TAXES = register("QUAD TAXES!!!", id = "QUAD_TAXES")
    val DOUBLE_MOBS_HP = register("DOUBLE MOBS HP!!!", id = "DOUBLE_MOBS_HP")
    val MOAR_SKILLZ = register("MOAR SKILLZ!!!", id = "MOAR_SKILLZ")

    // Aura
    val FUNDRAISING = register("Fundraising")
    val MINION_UNION = register("Minion Union")
    val UNIVERSAL_INCOME = register("Universal Income")
    val WORK_BETTER = register("Work Better")
    val WORK_HARDER = register("Work Harder")
    val WORK_SMARTER = register("Work Smarter")
    //endregion

    fun reset() = perks.forEach { it.active = false }

    fun getPerkById(id: String): MayorPerk? = _perks[id]
    fun getPerk(perkName: String) = perks.find { it.perkName == perkName }

    internal fun register(perkName: String, id: String = perkName.toScreamingSnakeCase()): MayorPerk {
        return _perks.getOrPut(id) { MayorPerk(id, perkName) }
    }
}

//? < 26.1 {
/*//region Old
@RemoveNextVersion(ReplaceWith("MayorCandidate"))
enum class Candidate(val mayorCandidate: MayorCandidate) {
    AATROX(MayorCandidates.AATROX),
    COLE(MayorCandidates.COLE),
    DIANA(MayorCandidates.DIANA),
    DIAZ(MayorCandidates.DIAZ),
    FINNEGAN(MayorCandidates.FINNEGAN),
    FOXY(MayorCandidates.FOXY),
    MARINA(MayorCandidates.MARINA),
    PAUL(MayorCandidates.PAUL),
    SCORPIUS(MayorCandidates.SCORPIUS),
    JERRY(MayorCandidates.JERRY),
    DERPY(MayorCandidates.DERPY),
    AURA(MayorCandidates.AURA),
    UNKNOWN(MayorCandidates.AATROX),
    ;

    val candidateName: String by mayorCandidate::candidateName
    val perks: Array<out Perk> = mayorCandidate.perks.mapNotNull(Perk::fromMayorPerk).toTypedArray()

    val activePerks get() = perks.filter { it.active }
    val isActive: Boolean by mayorCandidate::isActive
    val isSpecial: Boolean by mayorCandidate::isSpecial
    override fun toString(): String = candidateName

    companion object {
        internal fun fromMayorCandidate(mayorCandidate: MayorCandidate): Candidate {
            return entries.find { it.mayorCandidate == mayorCandidate } ?: UNKNOWN
        }
        fun getCandidate(candidateName: String): Candidate? = entries.find { it.candidateName == candidateName }
    }
}

@RemoveNextVersion(ReplaceWith("MayorPerk"))
enum class Perk(val mayorPerk: MayorPerk) {
    SLASHED_PRICING(MayorPerks.SLASHED_PRICING),
    SLAYER_XP_BUFF(MayorPerks.SLAYER_XP_BUFF),
    PATHFINDER(MayorPerks.PATHFINDER),
    PROSPECTION(MayorPerks.PROSPECTION),
    MINING_XP_BUFF(MayorPerks.MINING_XP_BUFF),
    MINING_FIESTA(MayorPerks.MINING_FIESTA),
    MOLTEN_FORGE(MayorPerks.MOLTEN_FORGE),
    LUCKY(MayorPerks.LUCKY),
    MYTHOLOGICAL_RITUAL(MayorPerks.MYTHOLOGICAL_RITUAL),
    PET_XP_BUFF(MayorPerks.PET_XP_BUFF),
    SHARING_IS_CARING(MayorPerks.SHARING_IS_CARING),
    SHOPPING_SPREE(MayorPerks.SHOPPING_SPREE),
    VOLUME_TRADING(MayorPerks.VOLUME_TRADING),
    STOCK_EXCHANGE(MayorPerks.STOCK_EXCHANGE),
    LONG_TERM_INVESTMENT(MayorPerks.LONG_TERM_INVESTMENT),
    PELT_POCALYPSE(MayorPerks.PELT_POCALYPSE),
    GOATED(MayorPerks.GOATED),
    BLOOMING_BUSINESS(MayorPerks.BLOOMING_BUSINESS),
    PEST_ERADICATOR(MayorPerks.PEST_ERADICATOR),
    SWEET_BENEVOLENCE(MayorPerks.SWEET_BENEVOLENCE),
    A_TIME_FOR_GIVING(MayorPerks.A_TIME_FOR_GIVING),
    CHIVALROUS_CARNIVAL(MayorPerks.CHIVALROUS_CARNIVAL),
    EXTRA_EVENT(MayorPerks.EXTRA_EVENT),
    FISHING_XP_BUFF(MayorPerks.FISHING_XP_BUFF),
    LUCK_OF_THE_SEA(MayorPerks.LUCK_OF_THE_SEA),
    FISHING_FESTIVAL(MayorPerks.FISHING_FESTIVAL),
    DOUBLE_TROUBLE(MayorPerks.DOUBLE_TROUBLE),
    MARAUDER(MayorPerks.MARAUDER),
    EZPZ(MayorPerks.EZPZ),
    BENEDICTION(MayorPerks.BENEDICTION),
    BRIBE(MayorPerks.BRIBE),
    DARKER_AUCTIONS(MayorPerks.DARKER_AUCTIONS),
    PERKPOCALYPSE(MayorPerks.PERKPOCALYPSE),
    STATSPOCALYPSE(MayorPerks.STATSPOCALYPSE),
    JERRYPOCALYPSE(MayorPerks.JERRYPOCALYPSE),
    TURBO_MINIONS(MayorPerks.TURBO_MINIONS),
    QUAD_TAXES(MayorPerks.QUAD_TAXES),
    DOUBLE_MOBS_HP(MayorPerks.DOUBLE_MOBS_HP),
    MOAR_SKILLZ(MayorPerks.MOAR_SKILLZ),
    FUNDRAISING(MayorPerks.FUNDRAISING),
    MINION_UNION(MayorPerks.MINION_UNION),
    UNIVERSAL_INCOME(MayorPerks.UNIVERSAL_INCOME),
    WORK_BETTER(MayorPerks.WORK_BETTER),
    WORK_HARDER(MayorPerks.WORK_HARDER),
    WORK_SMARTER(MayorPerks.WORK_SMARTER),
    ;

    val perkName: String by mayorPerk::perkName

    val active: Boolean by mayorPerk::active
    var description: String by mayorPerk::description

    companion object {
        fun reset() = MayorPerks.reset()

        internal fun fromMayorPerk(mayorPerk: MayorPerk): Perk? {
            return entries.find { it.mayorPerk == mayorPerk }
        }
        fun getPerk(perkName: String): Perk? = entries.find { it.perkName == perkName }
    }
}
//endregion
*///? }
