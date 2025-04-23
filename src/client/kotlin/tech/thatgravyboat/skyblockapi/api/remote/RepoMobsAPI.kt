package tech.thatgravyboat.skyblockapi.api.remote

import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.mobs.Mob
import tech.thatgravyboat.skyblockapi.modules.Module

@Module
object RepoMobsAPI {

    private val cache: MutableMap<String, Mob?> = mutableMapOf()

    fun getMobOrNull(id: String): Mob? = cache.getOrPut(id.uppercase()) {
        RepoAPI.mobs().getMob(id)
    }

}
