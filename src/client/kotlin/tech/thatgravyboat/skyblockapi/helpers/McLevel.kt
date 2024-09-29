package tech.thatgravyboat.skyblockapi.helpers

import net.minecraft.core.RegistryAccess
import net.minecraft.world.level.Level

object McLevel {

    val hasLevel: Boolean
        get() = McClient.self.level != null

    val self: Level
        get() = McClient.self.level!!

    val registry: RegistryAccess
        get() = self.registryAccess()
}
