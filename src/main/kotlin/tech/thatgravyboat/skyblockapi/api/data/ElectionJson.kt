package tech.thatgravyboat.skyblockapi.api.data

import me.owdding.ktcodecs.GenerateCodec

@GenerateCodec
data class ElectionJson(
    val mayor: MayorJson,
    val current: ElectionInfo?,
)

@GenerateCodec
data class MayorJson(
    val key: String,
    val name: String,
    val perks: List<PerkJson>,
    val minister: MinisterJson?,
    val election: ElectionInfo,
)

@GenerateCodec
data class PerkJson(
    val name: String,
    val description: String,
    val minister: Boolean = false,
)

@GenerateCodec
data class MinisterJson(
    val key: String,
    val name: String,
    val perk: PerkJson?,
)

@GenerateCodec
data class ElectionInfo(
    val year: Int,
    val candidates: List<CandidateJson>,
)

@GenerateCodec
data class CandidateJson(
    val key: String,
    val name: String,
    val perks: List<PerkJson>,
    val votes: Int,
)
