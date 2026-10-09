package tech.thatgravyboat.skyblockapi.api.profile.maxwell

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.IncludedCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

public data class MaxwellPower(val name: String, val internalName: String) {
    companion object {
        @IncludedCodec(keyable = true)
        public val CODEC: Codec<MaxwellPower> = SkyblockAPICodecs.getCodec<String>().xmap(MaxwellPowers::getById, MaxwellPower::internalName)
    }
}

@Suppress("unused")
public object MaxwellPowers {

    private val registeredPowers = mutableMapOf<String, MaxwellPower>()

    public fun getByName(name: String): MaxwellPower? {
        return registeredPowers.values.find { it.name.equals(name, true) }
    }

    public fun getById(id: String): MaxwellPower = registeredPowers[id] ?: NO_POWER

    // For powers that are obtained with power stones, the key is the ID of the power stone item
    private fun register(key: String, name: String): MaxwellPower {
        return registeredPowers.getOrPut(key) { MaxwellPower(name, key) }
    }

    public val NO_POWER = register("NO_POWER", "No Power")
    public val FORTUITOUS = register("FORTUITOUS", "Fortuitous")
    public val PRETTY = register("PRETTY", "Pretty")
    public val PROTECTED = register("PROTECTED", "Protected")
    public val SIMPLE = register("SIMPLE", "Simple")
    public val WARRIOR = register("WARRIOR", "Warrior")
    public val COMMANDO = register("COMMANDO", "Commando")
    public val DISCIPLINED = register("DISCIPLINED", "Disciplined")
    public val INSPIRED = register("INSPIRED", "Inspired")
    public val OMINOUS = register("OMINOUS", "Ominous")
    public val PREPARED = register("PREPARED", "Prepared")
    public val SILKY = register("LUXURIOUS_SPOOL", "Silky")
    public val SWEET = register("ROCK_CANDY", "Sweet")
    public val BLOODY = register("BEATING_HEART", "Bloody")
    public val ITCHY = register("FURBALL", "Itchy")
    public val SIGHTED = register("ENDER_MONACLE", "Sighted")
    public val ADEPT = register("END_STONE_SHULKER", "Adept")
    public val MYTHICAL = register("OBSIDIAN_TABLET", "Mythical")
    public val FORCEFUL = register("ACACIA_BIRDHOUSE", "Forceful")
    public val SHADED = register("DARK_ORB", "Shaded")
    public val STRONG = register("MANDRAA", "Strong")
    public val DEMONIC = register("HORNS_OF_TORMENT", "Demonic")
    public val PLEASANT = register("PRECIOUS_PEARL", "Pleasant")
    public val HURTFUL = register("MAGMA_URCHIN", "Hurtful")
    public val BIZARRE = register("ECCENTRIC_PAINTING", "Bizarre")
    public val HEALTHY = register("VITAMIN_DEATH", "Healthy")
    public val SLENDER = register("HAZMAT_ENDERMAN", "Slender")
    public val SCORCHING = register("SCORCHED_BOOKS", "Scorching")
    public val CRUMBLY = register("CHOCOLATE_CHIP", "Crumbly")
    public val BUBBA = register("BUBBA_BLISTER", "Bubba")
    public val SANGUISUGE = register("DISPLACED_LEECH", "Sanguisuge")
    public val FROZEN = register("GLACITE_SHARD", "Frozen")
    public val BUTTERY = register("SUNFLOWER_BUTTER", "Buttery")
}
