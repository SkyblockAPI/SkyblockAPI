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
    public val AATROX = register("Aatrox", MayorPerks.SLASHED_PRICING, MayorPerks.SLAYER_XP_BUFF, MayorPerks.PATHFINDER)
    public val COLE = register("Cole", MayorPerks.PROSPECTION, MayorPerks.MINING_XP_BUFF, MayorPerks.MINING_FIESTA, MayorPerks.MOLTEN_FORGE)
    public val DIANA = register("Diana", MayorPerks.HUNTRESS_INTUITION, MayorPerks.MYTHOLOGICAL_RITUAL, MayorPerks.PET_XP_BUFF, MayorPerks.SHARING_IS_CARING)
    public val DIAZ = register("Diaz", MayorPerks.SHOPPING_SPREE, MayorPerks.VOLUME_TRADING, MayorPerks.STOCK_EXCHANGE, MayorPerks.LONG_TERM_INVESTMENT)
    public val FINNEGAN = register("Finnegan", MayorPerks.GRAND_FEAST, MayorPerks.GOATED, MayorPerks.BLOOMING_BUSINESS, MayorPerks.PEST_ERADICATOR)
    public val FOXY = register("Foxy", MayorPerks.SWEET_BENEVOLENCE, MayorPerks.A_TIME_FOR_GIVING, MayorPerks.CHIVALROUS_CARNIVAL, MayorPerks.EXTRA_EVENT)
    public val MARINA = register("Marina", MayorPerks.FISHING_XP_BUFF, MayorPerks.LUCK_OF_THE_SEA, MayorPerks.FISHING_FESTIVAL, MayorPerks.DOUBLE_TROUBLE)
    public val PAUL = register("Paul", MayorPerks.MARAUDER, MayorPerks.EZPZ, MayorPerks.BENEDICTION)

    // Special Mayors
    public val SCORPIUS = register("Scorpius", MayorPerks.BRIBE, MayorPerks.DARKER_AUCTIONS, isSpecial = true)
    public val JERRY = register("Jerry", MayorPerks.PERKPOCALYPSE, MayorPerks.STATSPOCALYPSE, MayorPerks.JERRYPOCALYPSE, isSpecial = true)
    public val DERPY = register("Derpy", MayorPerks.TURBO_MINIONS, MayorPerks.QUAD_TAXES, MayorPerks.DOUBLE_MOBS_HP, MayorPerks.MOAR_SKILLZ, isSpecial = true)
    public val AURA = register(
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

public enum class FoxyExtraEventType(val eventName: String) {
    SPOOKY_FESTIVAL("Spooky Festival"),
    MINING_FIESTA("Mining Fiesta"),
    FISHING_FESTIVAL("Fishing Festival"),
    UNKNOWN("Unknown");

    companion object {
        public fun fromDescription(description: String): FoxyExtraEventType {
            val strippedDescription = description.stripColor()
            return entries.firstOrNull { it != UNKNOWN && it.eventName in strippedDescription } ?: UNKNOWN
        }
    }
}

@Suppress("unused")
public object MayorPerks {
    private val _perks = mutableMapOf<String, MayorPerk>()
    public val perks: Collection<MayorPerk> by _perks::values
    internal val perksMap: Map<String, MayorPerk> get() = _perks

    //region Perks
    // Aatrox
    public val SLASHED_PRICING = register("SLASHED Pricing")
    public val SLAYER_XP_BUFF = register("Slayer XP Buff")
    public val PATHFINDER = register("Pathfinder")

    // Cole
    public val PROSPECTION = register("Prospection")
    public val MINING_XP_BUFF = register("Mining XP Buff")
    public val MINING_FIESTA = register("Mining Fiesta")
    public val MOLTEN_FORGE = register("Molten Forge")

    // Diana
    //? < 26.2
    //@RemoveNextVersion val LUCKY = register("Lucky!")
    public val HUNTRESS_INTUITION = register("Huntress' Intuition")
    public val MYTHOLOGICAL_RITUAL = register("Mythological Ritual")
    public val PET_XP_BUFF = register("Pet XP Buff")
    public val SHARING_IS_CARING = register("Sharing is Caring")

    // Diaz
    public val SHOPPING_SPREE = register("Shopping Spree")
    public val VOLUME_TRADING = register("Volume Trading")
    public val STOCK_EXCHANGE = register("Stock Exchange")
    public val LONG_TERM_INVESTMENT = register("Long Term Investment")

    // Finnegan
    //? < 26.2
    //@RemoveNextVersion val PELT_POCALYPSE = register("Pelt-pocalypse")
    public val GRAND_FEAST = register("Grand Feast", perkpocalypse = false)
    public val GOATED = register("GOATed", id = "GOATED")
    public val BLOOMING_BUSINESS = register("Blooming Business")
    public val PEST_ERADICATOR = register("Pest Eradicator")

    // Foxy
    public val SWEET_BENEVOLENCE = register("Sweet Benevolence")
    public val A_TIME_FOR_GIVING = register("A Time for Giving")
    public val CHIVALROUS_CARNIVAL = register("Chivalrous Carnival")
    public val EXTRA_EVENT = register("Extra Event")

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
    public val FISHING_XP_BUFF = register("Fishing XP Buff")
    public val LUCK_OF_THE_SEA = register("Luck of the Sea 2.0", id = "LUCK_OF_THE_SEA")
    public val FISHING_FESTIVAL = register("Fishing Festival")
    public val DOUBLE_TROUBLE = register("Double Trouble")

    // Paul
    public val MARAUDER = register("Marauder")
    public val EZPZ = register("EZPZ")
    public val BENEDICTION = register("Benediction")

    // Scorpius
    public val BRIBE = register("Bribe")
    public val DARKER_AUCTIONS = register("Darker Auctions")

    // Jerry
    public val PERKPOCALYPSE = register("Perkpocalypse")
    public val STATSPOCALYPSE = register("Statspocalypse")
    public val JERRYPOCALYPSE = register("Jerrypocalypse")

    // Derpy
    public val TURBO_MINIONS = register("TURBO MINIONS!!!", id = "TURBO_MINIONS")
    public val QUAD_TAXES = register("QUAD TAXES!!!", id = "QUAD_TAXES")
    public val DOUBLE_MOBS_HP = register("DOUBLE MOBS HP!!!", id = "DOUBLE_MOBS_HP")
    public val MOAR_SKILLZ = register("MOAR SKILLZ!!!", id = "MOAR_SKILLZ")

    // Aura
    public val FUNDRAISING = register("Fundraising")
    public val MINION_UNION = register("Minion Union")
    public val UNIVERSAL_INCOME = register("Universal Income")
    public val WORK_BETTER = register("Work Better")
    public val WORK_HARDER = register("Work Harder")
    public val WORK_SMARTER = register("Work Smarter")
    //endregion

    public fun reset() = perks.forEach { it.active = false }

    public fun getPerkById(id: String): MayorPerk? = _perks[id]
    public fun getPerk(perkName: String) = perks.find { it.perkName == perkName }

    internal fun register(perkName: String, id: String = perkName.toScreamingSnakeCase(), perkpocalypse: Boolean = true): MayorPerk {
        return _perks.getOrPut(id) { MayorPerk(id, perkName, perkpocalypse = perkpocalypse) }
    }
}
