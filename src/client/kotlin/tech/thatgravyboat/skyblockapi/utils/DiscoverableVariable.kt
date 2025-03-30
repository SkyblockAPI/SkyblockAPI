package tech.thatgravyboat.skyblockapi.utils

import kotlin.reflect.KProperty

data class DiscoverableVariable<T>(var value: T?, var discover: () -> T?) {
    operator fun getValue(slayerInfo: Any, property: KProperty<*>): T? {
        if (value == null) {
            value = discover()
        }

        return value
    }
}
