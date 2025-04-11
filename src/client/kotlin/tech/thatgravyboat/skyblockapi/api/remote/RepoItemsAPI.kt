package tech.thatgravyboat.skyblockapi.api.remote

import com.mojang.serialization.JsonOps
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.utils.Logger
import tech.thatgravyboat.skyblockapi.utils.extentions.asString
import tech.thatgravyboat.skyblockapi.utils.json.getPath
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

object RepoItemsAPI {

    private val cache: MutableMap<String, ItemStack?> = mutableMapOf()
    internal val nameCache by lazy {
        RepoAPI.items().items().mapNotNull { entry ->
            val json = entry.value.getPath("['components'].['minecraft:custom_name'].['text']") ?: return@mapNotNull null
            val text = Text.of(json.asString("")).stripped.takeIf { it.isNotEmpty() } ?: return@mapNotNull null
            text.lowercase() to entry.key.uppercase()
        }.toMap()
    }

    fun getItemOrNull(id: String): ItemStack? = cache.getOrPut(id.uppercase()) {
        val json = RepoAPI.items().getItem(id.uppercase()) ?: return@getOrPut null
        ItemStack.CODEC.parse(JsonOps.INSTANCE, json)
            .ifError { Logger.error(it.message()) }
            .result()
            .orElse(null)
    }

    fun getItem(id: String): ItemStack = getItemOrNull(id) ?: ItemStack(Items.BARRIER).apply {
        this.set(DataComponents.ITEM_NAME, Text.of(id))
    }

    fun getItemIdByName(name: String): String? = nameCache[name.lowercase()]
}
