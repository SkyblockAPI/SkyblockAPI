//? < 26.2 {
/*package tech.thatgravyboat.skyblockapi.api.remote

import tech.thatgravyboat.repolib.api.ReforgeStonesAPI.ReforgeData
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockReforgeStonesRepo

@Deprecated("Use SkyBlockReforgeStonesRepo instead", ReplaceWith("tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockReforgeStonesRepo"))
public object RepoReforgeStonesAPI {

    public fun getReforge(id: String): ReforgeData? = SkyBlockReforgeStonesRepo.get(id)
    public fun getReforgeByName(name: String): Pair<String, ReforgeData>? = SkyBlockReforgeStonesRepo.getByName(name)

    public fun ReforgeData.getApplyCosts(): Map<SkyBlockRarity, Long?> = SkyBlockRarity.entries.associateWith { applyCost()[it.name] }.filter { it.value != null }
    public fun ReforgeData.getApplyCost(rarity: SkyBlockRarity): Long? = getApplyCosts()[rarity]
}*///?}
