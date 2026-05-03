package tech.thatgravyboat.skyblockapi.api.area.dungeon

import net.minecraft.world.entity.player.Player
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.isRealPlayer

class DungeonPlayer(
    val name: String,
    dungeonClass: DungeonClass?,
    classLevel: Int?
) {
    var dead: Boolean = false
        internal set

    var dungeonClass: DungeonClass? = dungeonClass
        internal set
    var classLevel: Int? = classLevel
        internal set

    /**
     * The index of the order at which they appear in tablist.
     * Will be -1 if the player is dead, and dead players will not be counted for the index.
     */
    var index: Int = -1
        internal set

    val realPlayer: Player?
        get() = McLevel.players.find { it.isRealPlayer() && it.cleanName == name }

    internal fun missingData(): Boolean = dungeonClass == null || classLevel == null

    override fun toString(): String = "DungeonPlayer(name='$name', dead=$dead, dungeonClass=$dungeonClass, classLevel=$classLevel, index=$index)"

}
