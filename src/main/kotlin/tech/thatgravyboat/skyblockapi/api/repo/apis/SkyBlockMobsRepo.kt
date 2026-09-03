package tech.thatgravyboat.skyblockapi.api.repo.apis

import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.mobs.LootTable
import tech.thatgravyboat.repolib.api.mobs.Mob
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.utils.extentions.compoundTag
import tech.thatgravyboat.skyblockapi.utils.extentions.toData
import kotlin.jvm.optionals.getOrNull

object SkyBlockMobsRepo : RepoItemCache<String>("Mobs") {

    private const val ID_KEY = "skyblock-api:id"
    private val repo get() = RepoAPI.mobs()

    override fun create(key: String): LazyItemStack? {
        val baseItem = get(key)?.item?.let(::LazyItemStack) ?: return null

        return baseItem.withComponents {
            val existingTag = baseItem[DataComponents.CUSTOM_DATA]?.copyTag() ?: CompoundTag()
            existingTag.putString(ID_KEY, key)
            this[DataComponents.CUSTOM_DATA] = existingTag.toData()
        }
    }

    fun get(key: String): Mob? = this.repo.getMob(key)
    fun getLootTables(key: String): List<LootTable> = get(key)?.lootTables ?: emptyList()

    fun ItemStack.getMobId(): String? = this.get(DataComponents.CUSTOM_DATA)?.copyTag()?.getString(ID_KEY)?.getOrNull()
    fun LazyItemStack.getMobId(): String? = this[DataComponents.CUSTOM_DATA]?.copyTag()?.getString(ID_KEY)?.getOrNull()
}
