package tech.thatgravyboat.skyblockapi.api.data

import com.google.gson.annotations.Expose

data class ElectionJson(
    @Expose val mayor: MayorJson,
    @Expose val current: ElectionInfo,
)

data class MayorJson(
    @Expose val key: String,
    @Expose val name: String,
    @Expose val perks: List<PerkJson>,
    @Expose val minister: MinisterJson?,
    @Expose val election: ElectionInfo,
)

data class PerkJson(
    @Expose val name: String,
    @Expose val description: String,
    @Expose val minister: Boolean = false,
)

data class MinisterJson(
    @Expose val key: String,
    @Expose val name: String,
    @Expose val perk: PerkJson,
)

data class ElectionInfo(
    @Expose val year: Int,
    @Expose val candidates: List<CandidateJson>,
)

data class CandidateJson(
    @Expose val key: String,
    @Expose val name: String,
    @Expose val perks: List<PerkJson>,
    @Expose val votes: Int,
)
