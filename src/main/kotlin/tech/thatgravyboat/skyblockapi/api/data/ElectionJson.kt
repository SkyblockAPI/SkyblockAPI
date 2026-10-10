package tech.thatgravyboat.skyblockapi.api.data

import me.owdding.ktcodecs.GenerateCodec

@GenerateCodec
public data class ElectionJson(
    val mayor: MayorJson,
    val current: ElectionInfo?,
)

@GenerateCodec
public data class MayorJson(
    val key: String,
    val name: String,
    val perks: List<PerkJson>,
    val minister: MinisterJson?,
    val election: ElectionInfo,
)

@GenerateCodec
public data class PerkJson(
    val name: String,
    val description: String,
    val minister: Boolean = false,
)

@GenerateCodec
public data class MinisterJson(
    val key: String,
    val name: String,
    val perk: PerkJson?,
)

@GenerateCodec
public data class ElectionInfo(
    val year: Int,
    val candidates: List<CandidateJson>,
)

@GenerateCodec
public data class CandidateJson(
    val key: String,
    val name: String,
    val perks: List<PerkJson>,
    val votes: Int,
)
