package tech.thatgravyboat.skyblockapi.utils

import tech.thatgravyboat.skyblockapi.helpers.McClient

enum class McVersion {
    MC_1_21_5,
    MC_1_21_6,
    MC_1_21_7,
    MC_1_21_8,
    MC_1_21_9,
    MC_1_21_10,
    MC_1_21_11,
    ;

    val stringVersion = name.substringAfter("_").replace("_", ".")
    val isActive: Boolean = this.stringVersion == McClient.version.substringBefore(" ")
}

enum class McVersionGroup(vararg versions: McVersion) {
    MC_1_21_5(McVersion.MC_1_21_5),
    MC_1_21_6(
        McVersion.MC_1_21_6,
        McVersion.MC_1_21_7,
        McVersion.MC_1_21_8,
    ),
    MC_1_21_9(
        McVersion.MC_1_21_9,
        McVersion.MC_1_21_10,
    ),
    MC_1_21_11(McVersion.MC_1_21_11)
    ;

    val isActive = versions.any { it.isActive }
}
