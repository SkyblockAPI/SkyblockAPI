package tech.thatgravyboat.skyblockapi.api.area.dungeon

import net.minecraft.world.entity.player.Player
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.utils.extentions.isRealPlayer
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

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

    val realPlayer: Player?
        get() = McLevel.self.players().find { it.isRealPlayer() && it.name.stripped == name }

    internal fun missingData(): Boolean = dungeonClass == null || classLevel == null
}
