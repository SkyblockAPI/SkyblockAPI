package tech.thatgravyboat.skyblockapi.utils

import tech.thatgravyboat.skyblockapi.helpers.McClient

public enum class McVersion {
    @Deprecated("Minecraft 26.1 isn't supported anymore.") MC_26_1,
    MC_26_2,
    MC_26_3,
    MC_26_4,
    //? > 26.4
    //add new version!
    ;

    public val stringVersion: String = name.substringAfter("_").replace("_", ".")

    /** should match both yy.drop and yy.drop.patch */
    public val isActive: Boolean = this.stringVersion == McClient.version.substringBefore(" ") || this.stringVersion == McClient.version.substringBefore(" ").substringBeforeLast(".")
}

//? < 26.4 {
/*@Deprecated(message = "Used mc version instead!")
@tech.thatgravyboat.skyblockapi.RemoveNextVersion
public enum class McVersionGroup(vararg versions: McVersion) {
    MC_26_1(McVersion.MC_26_1),
    MC_26_2(McVersion.MC_26_2),
    MC_26_3(McVersion.MC_26_3),
    ;

    public val isActive: Boolean = versions.any { it.isActive }
}*///?}
