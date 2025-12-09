package tech.thatgravyboat.skyblockapi.api.remote.api

import me.owdding.ktmodules.Module
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore
import tech.thatgravyboat.repolib.api.AttributesAPI
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.RepoStatus
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnRepoStatus
import tech.thatgravyboat.skyblockapi.api.events.misc.RepoStatusEvent
import tech.thatgravyboat.skyblockapi.platform.Identifiers
import tech.thatgravyboat.skyblockapi.utils.builders.ItemBuilder
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.asComponent
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.italic

@Module
object RepoAttributeAPI {

    private val attributeIdMap: MutableMap<String, AttributesAPI.Attribute> = mutableMapOf()
    private val cache: MutableMap<String, ItemStack?> = mutableMapOf()

    @Subscription(RepoStatusEvent::class)
    @OnRepoStatus(RepoStatus.SUCCESS)
    fun onRepoReady() {
        attributeIdMap.putAll(RepoAPI.attributes().attributes().values.associateBy { it.attributeId().lowercase() })
    }

    fun getAttributeDataById(id: String): AttributesAPI.Attribute? {
        if (!RepoAPI.isInitialized()) return null
        return attributeIdMap[id.lowercase()] ?: RepoAPI.attributes().getAttribute(id)
    }

    fun getAttributeByIdOrNull(id: String): ItemStack? {
        if (!RepoAPI.isInitialized()) return null
        return cache.getOrPut(id.lowercase()) {
            val attribute = attributeIdMap[id.lowercase()] ?: RepoAPI.attributes().getAttribute(id)
            if (attribute == null) return@getOrPut null

            val item = Identifiers.parse(attribute.item().lowercase())?.let { BuiltInRegistries.ITEM.getValue(it) }
                ?.takeUnless { it == Items.AIR }
                ?: Items.BARRIER

            ItemBuilder(item) {
                if (attribute.texture() != null) {
                    copyFrom(createSkull(attribute.texture()!!))
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
    }

}
