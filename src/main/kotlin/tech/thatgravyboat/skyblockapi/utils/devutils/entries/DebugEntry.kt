package tech.thatgravyboat.skyblockapi.utils.devutils.entries

import net.minecraft.resources.Identifier
import tech.thatgravyboat.skyblockapi.utils.devutils.DevUtils

public sealed interface DebugEntry {
    public val location: Identifier
    public val description: String

    public val devUtils: DevUtils

    public fun canSetDefault(): Boolean = devUtils.supportsChangingDefault
    public fun hasDescription(): Boolean {
        return description.isNotBlank() && description != location.toString() && description != location.path
    }

    public fun splitLocation(): List<String> = buildList {
        add(location.namespace)
        addAll(location.path.split('/', '.', '-'))
    }
}
