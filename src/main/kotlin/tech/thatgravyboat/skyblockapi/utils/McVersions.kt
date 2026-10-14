package tech.thatgravyboat.skyblockapi.utils

import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.helpers.McClient

public enum class McVersion {
    //? < 26.2 {
    /*MC_1_21_9,
    MC_1_21_10,
    MC_1_21_11,*///?}
    MC_26_1,
    MC_26_2,
    MC_26_3,
    //? > 26.3
    //add new version!
    ;

    public val stringVersion: String = name.substringAfter("_").replace("_", ".")

    /** should match both yy.drop and yy.drop.patch */
    public val isActive: Boolean = this.stringVersion == McClient.version.substringBefore(" ") || this.stringVersion == McClient.version.substringBefore(" ").substringBeforeLast(".")
}

@Deprecated(message = "Used mc version instead!")
@RemoveNextVersion
public enum class McVersionGroup(vararg versions: McVersion) {
    //? < 26.2 {
    /*MC_1_21_9(
        McVersion.MC_1_21_9,
        McVersion.MC_1_21_10,
    ),
    MC_1_21_11(McVersion.MC_1_21_11),
    *///? }
    MC_26_1(McVersion.MC_26_1),
    MC_26_2(McVersion.MC_26_2),
    MC_26_3(McVersion.MC_26_3),
    ;

    public val isActive: Boolean = versions.any { it.isActive }
}
