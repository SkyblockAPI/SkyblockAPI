package tech.thatgravyboat.skyblockapi.api.repo.apis

import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.RunesAPI.Rune
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack

object SkyBlockRunesRepo : RepoItemCacheAsQuery<SkyBlockRunesRepo.Query>("Runes", ::Query) {

    private val repo get() = RepoAPI.runes()

    override fun create(key: Query): LazyItemStack? {
        val rune = (if (key.tier == null) this.get(key.id)?.maxByOrNull(Rune::tier) else this.getTier(key.id, key.tier!!)) ?: return null
        return rune.item.let(::LazyItemStack)
    }

    fun get(id: String): List<Rune>? = ifInitialized { this.repo.getRunes(id) }
    fun getTier(id: String, tier: Int): Rune? = get(id)?.find { it.tier() == tier }

    data class Query(
        var id: String = "",
        var tier: Int? = null,
    )
}
