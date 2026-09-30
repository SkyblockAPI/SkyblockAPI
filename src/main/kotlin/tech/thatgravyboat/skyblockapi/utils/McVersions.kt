package tech.thatgravyboat.skyblockapi.utils

import tech.thatgravyboat.skyblockapi.helpers.McClient

enum class McVersion {
    MC_26_1,
    MC_26_2,
    MC_26_3,
    MC_26_4,
    //? > 26.4
    //add new version!
    ;

    val stringVersion = name.substringAfter("_").replace("_", ".")

    /** should match both yy.drop and yy.drop.patch */
    val isActive: Boolean = this.stringVersion == McClient.version.substringBefore(" ") || this.stringVersion == McClient.version.substringBefore(" ").substringBeforeLast(".")
}

//? < 26.4 {
/*@Deprecated(message = "Used mc version instead!")
@tech.thatgravyboat.skyblockapi.RemoveNextVersion
enum class McVersionGroup(vararg versions: McVersion) {
    MC_26_1(McVersion.MC_26_1),
    MC_26_2(McVersion.MC_26_2),
    MC_26_3(McVersion.MC_26_3),
    ;

    val isActive = versions.any { it.isActive }
}*///?}
