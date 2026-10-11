package tech.thatgravyboat.skyblockapi.api.repo.apis

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import me.owdding.ktmodules.Module
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.RunesAPI.Rune
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockRunesRepo.Query

private val schema: RepoItemQuerySchema<Query>.() -> Unit = {
    field("id", StringArgumentType.string(), Query::id) { suggestions ->
        if (!RepoAPI.isInitialized()) return@field
        RepoAPI.runes().runes().keys.forEach(suggestions)
    }
    optionalField("tier", IntegerArgumentType.integer(1), Query::tier)
}


@Module
public object SkyBlockRunesRepo : RepoItemCacheAsQuery<Query>("Runes", ::Query, schema) {

    private val repo get() = RepoAPI.runes()

    override fun create(key: Query): LazyItemStack? {
        val rune = (if (key.tier == null) this.get(key.id)?.maxByOrNull(Rune::tier) else this.getTier(key.id, key.tier!!)) ?: return null
        return rune.item.let(::LazyItemStack)
    }

    public fun get(id: String): List<Rune>? = ifInitialized { this.repo.getRunes(id) }
    public fun getTier(id: String, tier: Int): Rune? = get(id)?.find { it.tier() == tier }

    public data class Query(
        var id: String = "",
        var tier: Int? = null,
    )
}
