package tech.thatgravyboat.skyblockapi.utils.json

import net.minecraft.core.HolderLookup
import net.minecraft.core.HolderOwner
import net.minecraft.core.Registry
import net.minecraft.resources.RegistryOps
import net.minecraft.resources.ResourceKey
import java.util.Optional
import java.util.concurrent.ConcurrentHashMap

internal class LenientHolderOwner<T> : HolderOwner<T>

internal class LenientHolderLookupAdapter(private val provider: HolderLookup.Provider) : RegistryOps.RegistryInfoLookup {

    private val cache = ConcurrentHashMap<ResourceKey<out Registry<*>>, Optional<out RegistryOps.RegistryInfo<*>>>()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> lookup(key: ResourceKey<out Registry<out T?>?>): Optional<RegistryOps.RegistryInfo<T>> {
        return this.cache.computeIfAbsent(key) { key ->
            this.provider.lookup(key).map { lookup ->
                RegistryOps.RegistryInfo<Any>(LenientHolderOwner<Any>(), lookup, lookup.registryLifecycle())
            }
        } as Optional<RegistryOps.RegistryInfo<T>>
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LenientHolderLookupAdapter) return false
        return this.provider == other.provider
    }

    override fun hashCode(): Int {
        return this.provider.hashCode()
    }
}
