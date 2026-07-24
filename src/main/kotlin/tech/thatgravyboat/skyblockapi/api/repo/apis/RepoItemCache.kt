package tech.thatgravyboat.skyblockapi.api.repo.apis

import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.RegistryOps
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.utils.Logger
import tech.thatgravyboat.skyblockapi.utils.extentions.ItemStack
import tech.thatgravyboat.skyblockapi.utils.text.Text

abstract class RepoItemCache<K>(private val name: String) {

    private val cache: MutableMap<K, LazyItemStack?> = mutableMapOf()

    init {
        if (repos.contains(name)) {
            Logger.warn("RepoItemCache with name '$name' already exists. This may cause issues with cache invalidation.")
        }

        SkyBlockAPI.eventBus.register<ServerChangeEvent> {
            this.cache.values.forEach { it?.invalidate() }
        }
    }

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

        private val repos: MutableSet<String> = mutableSetOf()

        private val registryOps by lazy {
            val registryAccess = McLevel.self?.registryAccess()
            if (registryAccess == null) {
                Logger.error("No Registry Access found.")
                null
            } else RegistryOps.create(JsonOps.INSTANCE, registryAccess)
        }

        protected fun LazyItemStack(json: JsonObject): LazyItemStack? = LazyItemStack.CODEC
            .parse(registryOps, json)
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
