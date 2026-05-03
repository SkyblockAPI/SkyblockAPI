package tech.thatgravyboat.skyblockapi.api.repo.apis

import com.mojang.authlib.properties.Property
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.RunesAPI.Rune
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.platform.ResolvableProfile
import tech.thatgravyboat.skyblockapi.utils.text.Text

object SkyBlockRunesRepo : RepoItemCacheAsQuery<SkyBlockRunesRepo.Query>("Runes", ::Query) {

    private val repo get() = RepoAPI.runes()

    override fun create(key: Query): LazyItemStack? {
        val rune = (if (key.tier == null) this.get(key.id)?.maxByOrNull(Rune::tier) else this.getTier(key.id, key.tier!!)) ?: return null

        return LazyItemStack(Items.PLAYER_HEAD) {
            this[DataComponents.PROFILE] = ResolvableProfile { put("textures", Property("textures", rune.texture())) }
            this[DataComponents.CUSTOM_NAME] = Text.of(rune.name())
            this[DataComponents.LORE] = ItemLore(rune.lore().map(Text::of))
        }
    }

    fun get(id: String): List<Rune>? = ifInitialized { this.repo.getRunes(id) }
    fun getTier(id: String, tier: Int): Rune? = get(id)?.find { it.tier() == tier }

    data class Query(
        var id: String = "",
        var tier: Int? = null
    )
}
