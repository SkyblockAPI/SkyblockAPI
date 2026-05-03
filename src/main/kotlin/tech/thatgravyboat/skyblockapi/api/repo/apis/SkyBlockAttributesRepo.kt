package tech.thatgravyboat.skyblockapi.api.repo.apis

import com.mojang.authlib.properties.Property
import me.owdding.ktmodules.Module
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore
import tech.thatgravyboat.repolib.api.AttributesAPI
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.RepoStatus
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnRepoStatus
import tech.thatgravyboat.skyblockapi.api.events.misc.RepoStatusEvent
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import tech.thatgravyboat.skyblockapi.platform.ResolvableProfile
import tech.thatgravyboat.skyblockapi.utils.extentions.compoundTag
import tech.thatgravyboat.skyblockapi.utils.extentions.putCompound
import tech.thatgravyboat.skyblockapi.utils.extentions.toData
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.asComponent
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.italic

@Module
object SkyBlockAttributesRepo : RepoItemCache<String>("Attributes") {

    private val attributes: MutableMap<String, AttributesAPI.Attribute> = mutableMapOf()
    private val repo get() = RepoAPI.attributes()

    override fun create(key: String): LazyItemStack? {
        val attribute = attributes[key.lowercase()] ?: this.repo.getAttribute(key)
        if (attribute == null) return null

        val item = Identifiers.parse(attribute.item().lowercase())
            ?.let(BuiltInRegistries.ITEM::getValue)
            ?.takeUnless { it == Items.AIR }
            ?: Items.BARRIER

        return LazyItemStack(item.takeIf { attribute.texture() != null } ?: Items.PLAYER_HEAD) {
            if (attribute.texture() != null) {
                this[DataComponents.PROFILE] = ResolvableProfile { put("textures", Property("textures", attribute.texture())) }
            }
            this[DataComponents.ITEM_NAME] = attribute.shardName().asComponent()
            this[DataComponents.CUSTOM_NAME] = Text.of(attribute.shardName()) {
                this.italic = false
                runCatching {
                    this.color = SkyBlockRarity.valueOf(attribute.rarity()).color
                }
            }

            val rawLore = attribute.lore()
            val lore = rawLore.map { it.asComponent() }.toMutableList()
                .also { it.addFirst(Text.of(attribute.name()) { this.color = TextColor.GOLD }) }.toList()

            this[DataComponents.LORE] = ItemLore(lore, lore)
            this[DataComponents.CUSTOM_DATA] = compoundTag {
                putString("id", "ATTRIBUTE_SHARD")
                putCompound("attributes") {
                    putInt(attribute.id(), 1)
                }
            }.toData()
        }
    }

    @Subscription(RepoStatusEvent::class)
    @OnRepoStatus(RepoStatus.SUCCESS)
    fun onRepoReady() {
        this.attributes.putAll(this.repo.attributes().values.associateBy { it.attributeId().lowercase() })
    }

    fun get(id: String): AttributesAPI.Attribute? = ifInitialized {
        this.attributes[id.lowercase()] ?: this.repo.getAttribute(id)
    }
}
