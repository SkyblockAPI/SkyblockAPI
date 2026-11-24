package tech.thatgravyboat.skyblockapi.api.profile.maxwell

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.IncludedCodec
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.utils.RegisterableValues
import tech.thatgravyboat.skyblockapi.utils.devutils.debugToggle
import tech.thatgravyboat.skyblockapi.utils.devutils.entries.DebugToggle
import tech.thatgravyboat.skyblockapi.utils.extentions.toSnakeCase

public data class MaxwellPower(val name: String, val internalName: String) {
    public companion object {
        @IncludedCodec(keyable = true)
        public val CODEC: Codec<MaxwellPower> = Codec.STRING.xmap(MaxwellPowers::getById, MaxwellPower::internalName)
    }
}

@Suppress("unused")
@Module
public object MaxwellPowers : RegisterableValues<MaxwellPower>() {
    override val name: String = "power"
    override val debugToggle: DebugToggle = debugToggle("unknown.maxwell_power", "Warns when detecting a new unknown maxwell power")

    public fun getByNameOrNull(name: String): MaxwellPower? = registered.values.find { it.name.equals(name, true) }
    public fun getByName(name: String): MaxwellPower = getByNameOrNull(name) ?: register(name.toSnakeCase(), name)

    public fun getByIdOrNull(id: String): MaxwellPower? = registered[id]
    public fun getById(id: String): MaxwellPower {
        getByIdOrNull(id)?.let { return it }
        checkMissingKey(id)
        return NO_POWER
    }

    // For powers that are obtained with power stones, the key is the ID of the power stone item
    private fun register(key: String, name: String): MaxwellPower {
        return getOrCreate(key) { MaxwellPower(name, key) }
    }

    public val NO_POWER: MaxwellPower = register("NO_POWER", "No Power")
    public val FORTUITOUS: MaxwellPower = register("FORTUITOUS", "Fortuitous")
    public val PRETTY: MaxwellPower = register("PRETTY", "Pretty")
    public val PROTECTED: MaxwellPower = register("PROTECTED", "Protected")
    public val SIMPLE: MaxwellPower = register("SIMPLE", "Simple")
    public val WARRIOR: MaxwellPower = register("WARRIOR", "Warrior")
    public val COMMANDO: MaxwellPower = register("COMMANDO", "Commando")
    public val DISCIPLINED: MaxwellPower = register("DISCIPLINED", "Disciplined")
    public val INSPIRED: MaxwellPower = register("INSPIRED", "Inspired")
    public val OMINOUS: MaxwellPower = register("OMINOUS", "Ominous")
    public val PREPARED: MaxwellPower = register("PREPARED", "Prepared")
    public val SILKY: MaxwellPower = register("LUXURIOUS_SPOOL", "Silky")
    public val SWEET: MaxwellPower = register("ROCK_CANDY", "Sweet")
    public val BLOODY: MaxwellPower = register("BEATING_HEART", "Bloody")
    public val ITCHY: MaxwellPower = register("FURBALL", "Itchy")
    public val SIGHTED: MaxwellPower = register("ENDER_MONACLE", "Sighted")
    public val ADEPT: MaxwellPower = register("END_STONE_SHULKER", "Adept")
    public val MYTHICAL: MaxwellPower = register("OBSIDIAN_TABLET", "Mythical")
    public val FORCEFUL: MaxwellPower = register("ACACIA_BIRDHOUSE", "Forceful")
    public val SHADED: MaxwellPower = register("DARK_ORB", "Shaded")
    public val STRONG: MaxwellPower = register("MANDRAA", "Strong")
    public val DEMONIC: MaxwellPower = register("HORNS_OF_TORMENT", "Demonic")
    public val PLEASANT: MaxwellPower = register("PRECIOUS_PEARL", "Pleasant")
    public val HURTFUL: MaxwellPower = register("MAGMA_URCHIN", "Hurtful")
    public val BIZARRE: MaxwellPower = register("ECCENTRIC_PAINTING", "Bizarre")
    public val HEALTHY: MaxwellPower = register("VITAMIN_DEATH", "Healthy")
    public val SLENDER: MaxwellPower = register("HAZMAT_ENDERMAN", "Slender")
    public val SCORCHING: MaxwellPower = register("SCORCHED_BOOKS", "Scorching")
    public val CRUMBLY: MaxwellPower = register("CHOCOLATE_CHIP", "Crumbly")
    public val BUBBA: MaxwellPower = register("BUBBA_BLISTER", "Bubba")
    public val SANGUISUGE: MaxwellPower = register("DISPLACED_LEECH", "Sanguisuge")
    public val FROZEN: MaxwellPower = register("GLACITE_SHARD", "Frozen")
    public val BUTTERY: MaxwellPower = register("SUNFLOWER_BUTTER", "Buttery")
}
