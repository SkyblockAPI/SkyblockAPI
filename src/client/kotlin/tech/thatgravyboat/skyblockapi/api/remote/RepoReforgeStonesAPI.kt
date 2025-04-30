package tech.thatgravyboat.skyblockapi.api.remote

import tech.thatgravyboat.repolib.api.ReforgeStonesAPI.ReforgeData
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.modules.Module

@Module
object RepoReforgeStonesAPI {

    fun getReforge(id: String): ReforgeData? = RepoAPI.reforgeStones().getReforgeStone(id)
    fun getReforgeByName(name: String): Pair<String, ReforgeData>? =
        RepoAPI.reforgeStones().reforgeStones().entries.find { it.value.name.equals(name, true) }?.toPair()

    fun ReforgeData.getApplyCosts() = SkyBlockRarity.entries.associateWith { applyCost[it.name] }.filter { it.value != null }
    fun ReforgeData.getApplyCost(rarity: SkyBlockRarity) = getApplyCosts()[rarity]
}
