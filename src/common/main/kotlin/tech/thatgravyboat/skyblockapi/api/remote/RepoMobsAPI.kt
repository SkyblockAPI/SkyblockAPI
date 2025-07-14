package tech.thatgravyboat.skyblockapi.api.remote

import me.owdding.ktmodules.Module
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.mobs.Mob

@Module
object RepoMobsAPI {

    fun getMobOrNull(id: String): Mob? = RepoAPI.mobs().getMob(id)

}
