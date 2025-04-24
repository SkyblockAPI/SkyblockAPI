package tech.thatgravyboat.skyblockapi.api.remote

import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.mobs.Mob
import tech.thatgravyboat.skyblockapi.modules.Module

@Module
object RepoMobsAPI {

    fun getMobOrNull(id: String): Mob? = RepoAPI.mobs().getMob(id)

}
