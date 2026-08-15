package tech.thatgravyboat.skyblockapi.api.repo.apis

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import me.owdding.ktmodules.Module
import tech.thatgravyboat.repolib.api.EnchantsAPI
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockEnchantmentsRepo.Query
import tech.thatgravyboat.skyblockapi.utils.extentions.firstOrElseLast

private val schema: RepoItemQuerySchema<Query>.() -> Unit = {
    field("id", StringArgumentType.string(), Query::id)
    optionalField("level", IntegerArgumentType.integer(1), Query::level)
}

@Module
object SkyBlockEnchantmentsRepo : RepoItemCacheAsQuery<Query>("Enchantments", ::Query, schema) {

    private val repo get() = RepoAPI.enchantments()

    override fun create(key: Query): LazyItemStack? {
        val enchantment = get(key.id) ?: return null
        val enchantmentLevel = enchantment.levels().values.sortedBy(EnchantsAPI.EnchantLevel::level).firstOrElseLast { it.level() == key.level } ?: return null
        return enchantmentLevel.item.let(::LazyItemStack)
    }

    fun get(id: String): EnchantsAPI.Enchant? = ifInitialized { this.repo.getEnchantment(id) }

    data class Query(
        var id: String = "",
        var level: Int? = null,
    )
}
