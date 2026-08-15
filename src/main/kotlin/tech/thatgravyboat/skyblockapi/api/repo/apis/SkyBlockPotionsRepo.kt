package tech.thatgravyboat.skyblockapi.api.repo.apis

import tech.thatgravyboat.repolib.api.PotionsAPI
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.utils.extentions.firstOrElseLast

object SkyBlockPotionsRepo : RepoItemCacheAsQuery<SkyBlockPotionsRepo.Query>("Potions", ::Query) {

    private val repo get() = RepoAPI.potions()

    override fun create(key: Query): LazyItemStack? {
        val potion = get(key.id) ?: return null
        val level = potion.levels().values.sortedBy(PotionsAPI.PotionLevel::level).firstOrElseLast { it.level() == key.level }

        if (level == null) return null

        return level.item.let(::LazyItemStack)
    }

    fun get(id: String): PotionsAPI.Potion? = ifInitialized {
        if (id.lowercase() == "water") {
            return repo.potions().values.find { it.type == null }
        }
        return repo.getPotion(id)
    }

    data class Query(
        var id: String = "",
        var level: Int? = null,
    )
}
