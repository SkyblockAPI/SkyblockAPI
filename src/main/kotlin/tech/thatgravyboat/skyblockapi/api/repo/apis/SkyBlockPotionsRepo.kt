package tech.thatgravyboat.skyblockapi.api.repo.apis

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import me.owdding.ktmodules.Module
import tech.thatgravyboat.repolib.api.PotionsAPI
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockPotionsRepo.Query
import tech.thatgravyboat.skyblockapi.utils.extentions.firstOrElseLast

private val schema: RepoItemQuerySchema<Query>.() -> Unit = {
    field("id", StringArgumentType.string(), Query::id)
    optionalField("level", IntegerArgumentType.integer(1), Query::level)
}

@Module
object SkyBlockPotionsRepo : RepoItemCacheAsQuery<Query>("Potions", ::Query, schema) {

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
