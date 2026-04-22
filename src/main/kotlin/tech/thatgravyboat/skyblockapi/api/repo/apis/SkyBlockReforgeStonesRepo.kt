package tech.thatgravyboat.skyblockapi.api.repo.apis

import tech.thatgravyboat.repolib.api.ReforgeStonesAPI
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack

object SkyBlockReforgeStonesRepo : RepoItemCache<String>("Reforge Stones") {

    private val repo get() = RepoAPI.reforgeStones()

    override fun create(key: String): LazyItemStack? {
        if (!this.repo.reforgeStones().containsKey(key)) return null
        return RepoAPI.items().getItem(key)?.let(::LazyItemStack)
    }

    fun get(id: String): ReforgeStonesAPI.ReforgeData? = ifInitialized { this.repo.getReforgeStone(id) }

    fun getIdByName(name: String): String? = ifInitialized { this.repo.reforgeStones().entries.find { it.value.name().equals(name, true) }?.key }
    fun getByName(name: String): Pair<String, ReforgeStonesAPI.ReforgeData>? = getIdByName(name)?.let { id -> get(id)?.let { id to it } }
}
