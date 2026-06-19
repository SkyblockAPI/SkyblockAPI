package tech.thatgravyboat.skyblockapi.utils

import tech.thatgravyboat.skyblockapi.helpers.McClient

enum class McVersion {
    //? < 26.2 {
    /*MC_1_21_9,
    MC_1_21_10,
    MC_1_21_11,*///?}
    MC_26_1,
    MC_26_2,
    ;

    val stringVersion = name.substringAfter("_").replace("_", ".")
    val isActive: Boolean = this.stringVersion == McClient.version.substringBefore(" ")
}

enum class McVersionGroup(vararg versions: McVersion) {
    //? < 26.2 {
    /*MC_1_21_9(
        McVersion.MC_1_21_9,
        McVersion.MC_1_21_10,
    ),
    MC_1_21_11(McVersion.MC_1_21_11),
    *///? }
    MC_26_1(McVersion.MC_26_1),
    MC_26_2(McVersion.MC_26_2),
    ;

    val isActive = versions.any { it.isActive }
}
