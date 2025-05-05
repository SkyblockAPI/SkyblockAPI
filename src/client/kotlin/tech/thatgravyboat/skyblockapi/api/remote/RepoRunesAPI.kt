package tech.thatgravyboat.skyblockapi.api.remote

import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.RunesAPI.Rune

object RepoRunesAPI {

    fun getRuneById(id: String) = RepoAPI.runes().getRunes(id)
    fun getRune(id: String, tier: Int) = RepoAPI.runes().getRunes(id).find { it.tier == tier }

    fun getRune(string: String): Rune? {
        val split = string.split(":")
        if (split.size != 3) return null
        val id = split[1]
        val tier = split[2].toIntOrNull() ?: return null
        return getRune(id, tier)
    }

    fun Rune.getId() = buildString {
        append("rune:")
        append(this@getId.id)
        append(":")
        append(this@getId.tier)
    }
}
