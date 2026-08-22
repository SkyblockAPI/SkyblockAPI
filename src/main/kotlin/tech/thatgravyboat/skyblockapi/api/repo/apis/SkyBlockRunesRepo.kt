package tech.thatgravyboat.skyblockapi.api.repo.apis

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import me.owdding.ktmodules.Module
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.RunesAPI.Rune
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockRunesRepo.Query

private val schema: RepoItemQuerySchema<Query>.() -> Unit = {
    field("id", StringArgumentType.string(), Query::id, RepoAPI.runes().runes().keys)
    optionalField("tier", IntegerArgumentType.integer(1), Query::tier)
}


@Module
object SkyBlockRunesRepo : RepoItemCacheAsQuery<Query>("Runes", ::Query, schema) {

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
