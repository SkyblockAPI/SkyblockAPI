package tech.thatgravyboat.skyblockapi.api.repo.apis

import tech.thatgravyboat.repolib.api.EnchantsAPI
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.utils.extentions.firstOrElseLast

object SkyBlockEnchantmentsRepo : RepoItemCacheAsQuery<SkyBlockEnchantmentsRepo.Query>("Enchantments", ::Query) {

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
