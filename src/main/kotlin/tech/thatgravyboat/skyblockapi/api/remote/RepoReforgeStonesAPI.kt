//? < 26.2 {
/*package tech.thatgravyboat.skyblockapi.api.remote

import tech.thatgravyboat.repolib.api.ReforgeStonesAPI.ReforgeData
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockReforgeStonesRepo

@Deprecated("Use SkyBlockReforgeStonesRepo instead", ReplaceWith("tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockReforgeStonesRepo"))
object RepoReforgeStonesAPI {

    fun getReforge(id: String): ReforgeData? = SkyBlockReforgeStonesRepo.get(id)
    fun getReforgeByName(name: String): Pair<String, ReforgeData>? = SkyBlockReforgeStonesRepo.getByName(name)

    fun ReforgeData.getApplyCosts() = SkyBlockRarity.entries.associateWith { applyCost()[it.name] }.filter { it.value != null }
    fun ReforgeData.getApplyCost(rarity: SkyBlockRarity) = getApplyCosts()[rarity]
}*///?}
