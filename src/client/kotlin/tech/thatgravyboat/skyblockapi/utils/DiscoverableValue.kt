package tech.thatgravyboat.skyblockapi.utils

data class DiscoverableValue<T>(var discover: () -> T?): Lazy<T?> {
    private var _value: T? = null

    override val value: T?
        get() {
            if (_value == null) _value = discover()
            return _value
        }

    override fun isInitialized() = _value != null
}
