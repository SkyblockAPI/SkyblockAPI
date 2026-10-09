package tech.thatgravyboat.skyblockapi.api.data

import net.minecraft.util.TriState
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.area.hub.ElectionAPI
import tech.thatgravyboat.skyblockapi.api.data.stored.ElectionStorage
import tech.thatgravyboat.skyblockapi.utils.extentions.isInFuture
import tech.thatgravyboat.skyblockapi.utils.extentions.stripColor
import tech.thatgravyboat.skyblockapi.utils.extentions.toScreamingSnakeCase

@ConsistentCopyVisibility
public data class MayorCandidate internal constructor(
    val id: String,
    val candidateName: String,
    val perks: MutableSet<MayorPerk>,
    val isSpecial: Boolean,
) {
    val activePerks: Collection<MayorPerk> get() = perks.filter { it.active }
    val isActive: Boolean
        get() {
            if (ElectionAPI.mayor == this || ElectionAPI.minister == this) return true
            val (jerryCandidate, time) = ElectionAPI.currentJerryCandidate ?: return false
            return jerryCandidate == this && time.isInFuture()
        }

    internal fun addAllPerks(includeNonPerkpocalypse: Boolean = true): MayorCandidate = apply {
        perks.forEach { if (includeNonPerkpocalypse || it.perkpocalypse) it.active = true }
    }

    internal fun clearAllPerks(): MayorCandidate = apply { perks.forEach { it.active = false } }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MayorCandidate) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = candidateName
}

public object MayorCandidates {
    private val _mayors = mutableMapOf<String, MayorCandidate>()
    public val mayors: Collection<MayorCandidate> by _mayors::values
    internal val mayorsMap: Map<String, MayorCandidate> get() = _mayors

    //region Candidates
    public val AATROX: MayorCandidate = register("Aatrox", MayorPerks.SLASHED_PRICING, MayorPerks.SLAYER_XP_BUFF, MayorPerks.PATHFINDER)
    public val COLE: MayorCandidate = register("Cole", MayorPerks.PROSPECTION, MayorPerks.MINING_XP_BUFF, MayorPerks.MINING_FIESTA, MayorPerks.MOLTEN_FORGE)
    public val DIANA: MayorCandidate = register("Diana", MayorPerks.HUNTRESS_INTUITION, MayorPerks.MYTHOLOGICAL_RITUAL, MayorPerks.PET_XP_BUFF, MayorPerks.SHARING_IS_CARING)
    public val DIAZ: MayorCandidate = register("Diaz", MayorPerks.SHOPPING_SPREE, MayorPerks.VOLUME_TRADING, MayorPerks.STOCK_EXCHANGE, MayorPerks.LONG_TERM_INVESTMENT)
    public val FINNEGAN: MayorCandidate = register("Finnegan", MayorPerks.GRAND_FEAST, MayorPerks.GOATED, MayorPerks.BLOOMING_BUSINESS, MayorPerks.PEST_ERADICATOR)
    public val FOXY: MayorCandidate = register("Foxy", MayorPerks.SWEET_BENEVOLENCE, MayorPerks.A_TIME_FOR_GIVING, MayorPerks.CHIVALROUS_CARNIVAL, MayorPerks.EXTRA_EVENT)
    public val MARINA: MayorCandidate = register("Marina", MayorPerks.FISHING_XP_BUFF, MayorPerks.LUCK_OF_THE_SEA, MayorPerks.FISHING_FESTIVAL, MayorPerks.DOUBLE_TROUBLE)
    public val PAUL: MayorCandidate = register("Paul", MayorPerks.MARAUDER, MayorPerks.EZPZ, MayorPerks.BENEDICTION)

    // Special Mayors
    public val SCORPIUS: MayorCandidate = register("Scorpius", MayorPerks.BRIBE, MayorPerks.DARKER_AUCTIONS, isSpecial = true)
    public val JERRY: MayorCandidate = register("Jerry", MayorPerks.PERKPOCALYPSE, MayorPerks.STATSPOCALYPSE, MayorPerks.JERRYPOCALYPSE, isSpecial = true)
    public val DERPY: MayorCandidate = register("Derpy", MayorPerks.TURBO_MINIONS, MayorPerks.QUAD_TAXES, MayorPerks.DOUBLE_MOBS_HP, MayorPerks.MOAR_SKILLZ, isSpecial = true)
    public val AURA: MayorCandidate = register(
        "Aura",
        MayorPerks.FUNDRAISING,
        MayorPerks.MINION_UNION,
        MayorPerks.UNIVERSAL_INCOME,
        MayorPerks.WORK_BETTER,
        MayorPerks.WORK_HARDER,
        MayorPerks.WORK_SMARTER,
        isSpecial = true,
    )
    //endregion

    public fun getCandidateById(id: String): MayorCandidate? = _mayors[id]
    public fun getCandidate(candidateName: String): MayorCandidate? = mayors.find { it.candidateName == candidateName }

    internal fun register(
        candidateName: String,
        vararg perks: MayorPerk,
        id: String = candidateName.toScreamingSnakeCase(),
        isSpecial: Boolean = false,
    ): MayorCandidate {
        return _mayors.getOrPut(id) { MayorCandidate(id, candidateName, perks.toMutableSet(), isSpecial) }
    }
}

@ConsistentCopyVisibility
public data class MayorPerk internal constructor(
    val id: String,
    val perkName: String,
    var description: String = "Not available",
    val perkpocalypse: Boolean = true,
) {
    @RemoveNextVersion
    @Deprecated("Use MayorPerk.perkpocalypse instead.", ReplaceWith("perkpocalypse"))
    val perkapocalypse: Boolean get() = perkpocalypse

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

    // Try to load perk description from cache
    init {
        val description = ElectionStorage.getPerkDescription(id)
        if (description != null) this.description = description
    }
}

public enum class FoxyExtraEventType(public val eventName: String) {
    SPOOKY_FESTIVAL("Spooky Festival"),
    MINING_FIESTA("Mining Fiesta"),
    FISHING_FESTIVAL("Fishing Festival"),
    UNKNOWN("Unknown");

    public companion object {
        public fun fromDescription(description: String): FoxyExtraEventType {
            val strippedDescription = description.stripColor()
            return entries.firstOrNull { it != UNKNOWN && it.eventName in strippedDescription } ?: UNKNOWN
        }
    }
}

@Suppress("unused")
public object MayorPerks {
    internal val perksMap: Map<String, MayorPerk>
        field = mutableMapOf()
    public val perks: Collection<MayorPerk> by perksMap::values

    //region Perks
    // Aatrox
    public val SLASHED_PRICING: MayorPerk = register("SLASHED Pricing")
    public val SLAYER_XP_BUFF: MayorPerk = register("Slayer XP Buff")
    public val PATHFINDER: MayorPerk = register("Pathfinder")

    // Cole
    public val PROSPECTION: MayorPerk = register("Prospection")
    public val MINING_XP_BUFF: MayorPerk = register("Mining XP Buff")
    public val MINING_FIESTA: MayorPerk = register("Mining Fiesta")
    public val MOLTEN_FORGE: MayorPerk = register("Molten Forge")

    // Diana
    //? < 26.2
    //@RemoveNextVersion public val LUCKY: MayorPerk = register("Lucky!")
    public val HUNTRESS_INTUITION: MayorPerk = register("Huntress' Intuition")
    public val MYTHOLOGICAL_RITUAL: MayorPerk = register("Mythological Ritual")
    public val PET_XP_BUFF: MayorPerk = register("Pet XP Buff")
    public val SHARING_IS_CARING: MayorPerk = register("Sharing is Caring")

    // Diaz
    public val SHOPPING_SPREE: MayorPerk = register("Shopping Spree")
    public val VOLUME_TRADING: MayorPerk = register("Volume Trading")
    public val STOCK_EXCHANGE: MayorPerk = register("Stock Exchange")
    public val LONG_TERM_INVESTMENT: MayorPerk = register("Long Term Investment")

    // Finnegan
    //? < 26.2
    //@RemoveNextVersion public val PELT_POCALYPSE: MayorPerk = register("Pelt-pocalypse")
    public val GRAND_FEAST: MayorPerk = register("Grand Feast", perkpocalypse = false)
    public val GOATED: MayorPerk = register("GOATed", id = "GOATED")
    public val BLOOMING_BUSINESS: MayorPerk = register("Blooming Business")
    public val PEST_ERADICATOR: MayorPerk = register("Pest Eradicator")

    // Foxy
    public val SWEET_BENEVOLENCE: MayorPerk = register("Sweet Benevolence")
    public val A_TIME_FOR_GIVING: MayorPerk = register("A Time for Giving")
    public val CHIVALROUS_CARNIVAL: MayorPerk = register("Chivalrous Carnival")
    public val EXTRA_EVENT: MayorPerk = register("Extra Event")

    private var cachedFoxyEventType: FoxyExtraEventType? = null
    private var lastFoxyEventDescription: String? = null

    public val foxyExtraEventType: FoxyExtraEventType?
        get() {
            if (!EXTRA_EVENT.active) return null

            val currentDescription = EXTRA_EVENT.description

            if (lastFoxyEventDescription == currentDescription) return cachedFoxyEventType

            cachedFoxyEventType = FoxyExtraEventType.fromDescription(currentDescription)
            lastFoxyEventDescription = currentDescription
            return cachedFoxyEventType
        }

    // Marina
    public val FISHING_XP_BUFF: MayorPerk = register("Fishing XP Buff")
    public val LUCK_OF_THE_SEA: MayorPerk = register("Luck of the Sea 2.0", id = "LUCK_OF_THE_SEA")
    public val FISHING_FESTIVAL: MayorPerk = register("Fishing Festival")
    public val DOUBLE_TROUBLE: MayorPerk = register("Double Trouble")

    // Paul
    public val MARAUDER: MayorPerk = register("Marauder")
    public val EZPZ: MayorPerk = register("EZPZ")
    public val BENEDICTION: MayorPerk = register("Benediction")

    // Scorpius
    public val BRIBE: MayorPerk = register("Bribe")
    public val DARKER_AUCTIONS: MayorPerk = register("Darker Auctions")

    // Jerry
    public val PERKPOCALYPSE: MayorPerk = register("Perkpocalypse")
    public val STATSPOCALYPSE: MayorPerk = register("Statspocalypse")
    public val JERRYPOCALYPSE: MayorPerk = register("Jerrypocalypse")

    // Derpy
    public val TURBO_MINIONS: MayorPerk = register("TURBO MINIONS!!!", id = "TURBO_MINIONS")
    public val QUAD_TAXES: MayorPerk = register("QUAD TAXES!!!", id = "QUAD_TAXES")
    public val DOUBLE_MOBS_HP: MayorPerk = register("DOUBLE MOBS HP!!!", id = "DOUBLE_MOBS_HP")
    public val MOAR_SKILLZ: MayorPerk = register("MOAR SKILLZ!!!", id = "MOAR_SKILLZ")

    // Aura
    public val FUNDRAISING: MayorPerk = register("Fundraising")
    public val MINION_UNION: MayorPerk = register("Minion Union")
    public val UNIVERSAL_INCOME: MayorPerk = register("Universal Income")
    public val WORK_BETTER: MayorPerk = register("Work Better")
    public val WORK_HARDER: MayorPerk = register("Work Harder")
    public val WORK_SMARTER: MayorPerk = register("Work Smarter")
    //endregion

    public fun reset(): Unit = perks.forEach { it.active = false }

    public fun getPerkById(id: String): MayorPerk? = perksMap[id]
    public fun getPerk(perkName: String): MayorPerk? = perks.find { it.perkName == perkName }

    internal fun register(perkName: String, id: String = perkName.toScreamingSnakeCase(), perkpocalypse: Boolean = true): MayorPerk {
        return perksMap.getOrPut(id) { MayorPerk(id, perkName, perkpocalypse = perkpocalypse) }
    }
}
