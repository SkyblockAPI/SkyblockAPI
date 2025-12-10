package tech.thatgravyboat.skyblockapi.api.remote

import me.owdding.ktmodules.Module
import tech.thatgravyboat.repolib.api.ReforgeStonesAPI.ReforgeData
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity

@Module
object RepoReforgeStonesAPI {

    fun getReforge(id: String): ReforgeData? {
        if (!RepoAPI.isInitialized()) return null
        return RepoAPI.reforgeStones().getReforgeStone(id)
    }
    fun getReforgeByName(name: String): Pair<String, ReforgeData>? {
        if (!RepoAPI.isInitialized()) return null
        return RepoAPI.reforgeStones().reforgeStones()
            .entries
            .find { it.value.name().equals(name, true) }
            ?.toPair()
    }

    fun ReforgeData.getApplyCosts() = SkyBlockRarity.entries.associateWith { applyCost()[it.name] }.filter { it.value != null }
    fun ReforgeData.getApplyCost(rarity: SkyBlockRarity) = getApplyCosts()[rarity]
}
