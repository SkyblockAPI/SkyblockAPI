package tech.thatgravyboat.skyblockapi.utils.lazy

import me.owdding.ktmodules.Module
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents

fun <T : Any> registryBoundLazy(factory: () -> T): Lazy<T> = RegistryBoundLazy(factory)

class RegistryBoundLazy<out T : Any>(private val factory: () -> T) : Lazy<T> {

    private var cacheKey = -1
    private var _value: Any? = null

    override val value: T
        get() {
            val beforeSync = this._value
            if (this.cacheKey == registryCacheKey && beforeSync != null) {
                @Suppress("UNCHECKED_CAST")
                return beforeSync as T
            }

            return synchronized(this) {
                val duringSync = this._value
                if (this.cacheKey == registryCacheKey && duringSync != null) {
                    @Suppress("UNCHECKED_CAST")
                    duringSync as T
                } else {
                    val newValue = factory()
                    this._value = newValue
                    this.cacheKey = registryCacheKey
                    newValue
                }
            }
        }

    override fun isInitialized(): Boolean {
        return this.cacheKey == registryCacheKey && this._value != null
    }

    @Module
    companion object {

        private var registryCacheKey = -1

        init {
            ClientConfigurationConnectionEvents.COMPLETE.register { _, _ ->
                registryCacheKey++
            }
        }
    }
}
