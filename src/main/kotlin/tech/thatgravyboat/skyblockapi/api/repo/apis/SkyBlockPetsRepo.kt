package tech.thatgravyboat.skyblockapi.api.repo.apis

import com.mojang.authlib.properties.Property
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore
import tech.thatgravyboat.repolib.api.PetsAPI
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.LoreDataTypes
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.platform.ResolvableProfile
import tech.thatgravyboat.skyblockapi.utils.extentions.compoundTag
import tech.thatgravyboat.skyblockapi.utils.extentions.putCompound
import tech.thatgravyboat.skyblockapi.utils.extentions.toData
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.italic

object SkyBlockPetsRepo : RepoItemCacheAsQuery<SkyBlockPetsRepo.Query>("Pets", ::Query) {

    private val repo get() = RepoAPI.pets()

    override fun create(key: Query): LazyItemStack? {
        val data = RepoAPI.pets().getPet(key.id) ?: return null
        val pet = data.tiers()[key.rarity.name] ?: return null
        val skin = key.skin?.let { SkyBlockItemsRepo.getLazyItemStack("PET_SKIN_$it") }
        val skinRarity = skin?.let { LoreDataTypes.getRarityLine(it[DataComponents.LORE]) }?.second
        val name = Text.join(
            Text.of("[Lvl ${key.level}] ", TextColor.GRAY),
            Text.of(data.name(), key.rarity.color),
            if (key.skin != null) Text.of(" ✦", skinRarity?.color ?: TextColor.LIGHT_PURPLE) else null,
        ) {
            this.italic = false
        }
        val lore = ItemLore(pet.getFormattedLore(key.level, key.heldItem).map(Text::of))

        val customData = compoundTag {
            putString("id", "PET")
            putCompound("petInfo") {
                putString("type", key.id)
                putString("tier", key.rarity.name)
                putDouble("exp", 0.0)
                putInt("candyUsed", 0)
            }
        }.toData()

        return skin?.withComponents {
            this[DataComponents.CUSTOM_NAME] = name
            this[DataComponents.LORE] = lore
            this[DataComponents.CUSTOM_DATA] = customData
        } ?: LazyItemStack(Items.PLAYER_HEAD) {
            this[DataComponents.PROFILE] = ResolvableProfile { put("textures", Property("textures", pet.texture())) }
            this[DataComponents.CUSTOM_NAME] = name
            this[DataComponents.LORE] = lore
            this[DataComponents.CUSTOM_DATA] = customData
        }
    }

    fun get(id: String): PetsAPI.Data? = ifInitialized { this.repo.getPet(id) }

    data class Query(
        var id: String = "",
        var rarity: SkyBlockRarity = SkyBlockRarity.COMMON,
        var level: Int = 100,
        var skin: String? = null,
        var heldItem: String? = null,
    )
}
