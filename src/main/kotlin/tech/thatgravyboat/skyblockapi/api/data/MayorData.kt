package tech.thatgravyboat.skyblockapi.api.data

import net.minecraft.util.TriState
//? < 26.2
//import tech.thatgravyboat.skyblockapi.RemoveNextVersion
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
    val FINNEGAN = register("Finnegan", MayorPerks.GRAND_FEAST, MayorPerks.GOATED, MayorPerks.BLOOMING_BUSINESS, MayorPerks.PEST_ERADICATOR)
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MayorPerk) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
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
    //? < 26.2
    //@RemoveNextVersion val LUCKY = register("Lucky!")
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
    //? < 26.2
    //@RemoveNextVersion val PELT_POCALYPSE = register("Pelt-pocalypse")
    val GRAND_FEAST = register("Grand Feast")
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
