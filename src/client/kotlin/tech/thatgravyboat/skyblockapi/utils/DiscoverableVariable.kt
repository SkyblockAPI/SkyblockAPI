package tech.thatgravyboat.skyblockapi.utils

private object UNINITIALIZED_VALUE

data class DiscoverableVariable<T>(var discover: () -> T?): Lazy<T?> {
    private var _value: Any = UNINITIALIZED_VALUE

    override val value: T?
        get() {
            if (isInitialized()) {
                @Suppress("UNCHECKED_CAST")
                return _value as T
            }

            _value = discover()?: UNINITIALIZED_VALUE
            @Suppress("UNCHECKED_CAST")
            return _value.takeUnless { it == UNINITIALIZED_VALUE } as T?
        }

    override fun isInitialized() = _value !== UNINITIALIZED_VALUE
}
