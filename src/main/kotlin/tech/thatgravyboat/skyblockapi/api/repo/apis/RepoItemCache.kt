package tech.thatgravyboat.skyblockapi.api.repo.apis

import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.utils.Logger
import tech.thatgravyboat.skyblockapi.utils.extentions.ItemStack
import tech.thatgravyboat.skyblockapi.utils.text.Text

abstract class RepoItemCache<K> (private val name: String) {

    private val cache: MutableMap<K, LazyItemStack?> = mutableMapOf()

    protected abstract fun create(key: K): LazyItemStack?

    fun getLazyItemStack(key: K): LazyItemStack? = ifInitialized {
        cache.getOrPut(key) { create(key) }
    }

    fun getItemStack(key: K): ItemStack? = getLazyItemStack(key)?.create()
    fun getItemStackOrDefault(key: K): ItemStack = getLazyItemStack(key)?.create() ?: ItemStack(Items.BARRIER) {
        this[DataComponents.ITEM_NAME] = Text.of("Could not find item for key '$key' in $name")
    }

    protected inline fun <Return> ifInitialized(action: () -> Return?): Return? = if (RepoAPI.isInitialized()) {
        action()
    } else {
        null
    }

    protected open fun clear() {
        cache.clear()
    }

    companion object {

        protected fun LazyItemStack(json: JsonObject): LazyItemStack? = LazyItemStack.CODEC
            .parse(JsonOps.INSTANCE, json)
            .ifError { Logger.error(it.message()) }
            .result()
            .orElse(null)
    }
}

sealed class RepoItemCacheAsQuery<K>(name: String, private val factory: () -> K) : RepoItemCache<K>(name) {
    fun getLazyItemStack(query: K.() -> Unit): LazyItemStack? = this.getLazyItemStack(this.factory().apply(query))
    fun getItemStack(query: K.() -> Unit): ItemStack? = getItemStack(this.factory().apply(query))
    fun getItemStackOrDefault(query: K.() -> Unit): ItemStack = getItemStackOrDefault(this.factory().apply(query))
}
