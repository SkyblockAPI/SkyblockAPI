package tech.thatgravyboat.skyblockapi.api.profile.maxwell

import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.kcodec.IncludedCodec

data class MaxwellPower(val name: String, val internalName: String) {
    companion object {
        @IncludedCodec(keyable = true)
        val CODEC: Codec<MaxwellPower> = KCodec.getCodec<String>().xmap(MaxwellPowers::getById, MaxwellPower::internalName)
    }
}

@Suppress("unused")
object MaxwellPowers {

    private val registeredPowers = mutableMapOf<String, MaxwellPower>()

    fun getByName(name: String): MaxwellPower? {
        return registeredPowers.values.find { it.name.equals(name, true) }
    }

    fun getById(id: String): MaxwellPower = registeredPowers[id] ?: NO_POWER

    // For powers that are obtained with power stones, the key is the ID of the power stone item
    private fun register(key: String, name: String): MaxwellPower {
        return registeredPowers.getOrPut(key) { MaxwellPower(name, key) }
    }

    val NO_POWER = register("NO_POWER", "No Power")
    val FORTUITOUS = register("FORTUITOUS", "Fortuitous")
    val PRETTY = register("PRETTY", "Pretty")
    val PROTECTED = register("PROTECTED", "Protected")
    val SIMPLE = register("SIMPLE", "Simple")
    val WARRIOR = register("WARRIOR", "Warrior")
    val COMMANDO = register("COMMANDO", "Commando")
    val DISCIPLINED = register("DISCIPLINED", "Disciplined")
    val INSPIRED = register("INSPIRED", "Inspired")
    val OMINOUS = register("OMINOUS", "Ominous")
    val PREPARED = register("PREPARED", "Prepared")
    val SILKY = register("LUXURIOUS_SPOOL", "Silky")
    val SWEET = register("ROCK_CANDY", "Sweet")
    val BLOODY = register("BEATING_HEART", "Bloody")
    val ITCHY = register("FURBALL", "Itchy")
    val SIGHTED = register("ENDER_MONACLE", "Sighted")
    val ADEPT = register("END_STONE_SHULKER", "Adept")
    val MYTHICAL = register("OBSIDIAN_TABLET", "Mythical")
    val FORCEFUL = register("ACACIA_BIRDHOUSE", "Forceful")
    val SHADED = register("DARK_ORB", "Shaded")
    val STRONG = register("MANDRAA", "Strong")
    val DEMONIC = register("HORNS_OF_TORMENT", "Demonic")
    val PLEASANT = register("PRECIOUS_PEARL", "Pleasant")
    val HURTFUL = register("MAGMA_URCHIN", "Hurtful")
    val BIZARRE = register("ECCENTRIC_PAINTING", "Bizarre")
    val HEALTHY = register("VITAMIN_DEATH", "Healthy")
    val SLENDER = register("HAZMAT_ENDERMAN", "Slender")
    val SCORCHING = register("SCORCHED_BOOKS", "Scorching")
    val CRUMBLY = register("CHOCOLATE_CHIP", "Crumbly")
    val BUBBA = register("BUBBA_BLISTER", "Bubba")
    val SANGUISUGE = register("DISPLACED_LEECH", "Sanguisuge")
    val FROZEN = register("GLACITE_SHARD", "Frozen")

}
