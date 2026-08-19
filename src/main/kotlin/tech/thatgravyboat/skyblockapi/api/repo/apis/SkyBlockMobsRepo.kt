package tech.thatgravyboat.skyblockapi.api.repo.apis

import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.mobs.LootTable
import tech.thatgravyboat.repolib.api.mobs.Mob
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack

object SkyBlockMobsRepo : RepoItemCache<String>("Mobs") {

    private val repo get() = RepoAPI.mobs()

    override fun create(key: String): LazyItemStack? = get(key)?.item?.let(::LazyItemStack)

    fun get(key: String): Mob? = this.repo.getMob(key)
    fun getLootTables(key: String): List<LootTable> = get(key)?.lootTables ?: emptyList()
}
