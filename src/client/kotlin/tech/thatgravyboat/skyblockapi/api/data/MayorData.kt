package tech.thatgravyboat.skyblockapi.api.data

import tech.thatgravyboat.skyblockapi.api.data.Candidate.entries
import tech.thatgravyboat.skyblockapi.api.data.Perk.entries


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

    val isSpecial by lazy { this in setOf(SCORPIUS, JERRY, DERPY) }

    override fun toString() = candidateName

    companion object {
        fun getCandidate(candidateName: String): Candidate? = entries.find { it.candidateName == candidateName }
    }
}

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

    companion object {
        fun reset() {
            entries.forEach { it.active = false }
        }

        fun getPerk(perkName: String): Perk? = entries.find { it.perkName == perkName }
    }
}
