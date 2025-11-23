package tech.thatgravyboat.skyblockapi.api.datatype

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.impl.DataTypesRegistry
import tech.thatgravyboat.skyblockapi.utils.extentions.getCompoundTagFunctionByType
import tech.thatgravyboat.skyblockapi.utils.extentions.tag
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class DataType<T> @RemoveNextVersion constructor(
    val id: String,
    autoRegister: Boolean,
    val factory: (ItemStack) -> T?,
    val type: KType?,
) {
    @RemoveNextVersion
    constructor(id: String, autoRegister: Boolean, factory: (ItemStack) -> T?) : this(id, autoRegister, factory, null)

    @RemoveNextVersion
    constructor(id: String, factory: (ItemStack) -> T?) : this(id, true, factory)

    init {
        if (autoRegister) DataTypesRegistry.addDataType(this)
    }

    @Suppress("UNCHECKED_CAST")
    fun cast(value: Any?): T? = value as? T

    companion object {
        fun <T> of(id: String, type: KType, autoRegister: Boolean = true, factory: (ItemStack) -> T?): DataType<T> {
            return DataType(id, autoRegister, factory, type)
        }

        inline fun <reified T> of(id: String, autoRegister: Boolean = true, noinline factory: (ItemStack) -> T?): DataType<T> =
            of(id, typeOf<T>(), autoRegister, factory)

        fun <T : Any> simple(id: String, type: KType, tagName: String = id, autoRegister: Boolean = true): DataType<T> {
            val function = getCompoundTagFunctionByType<T>(type)
            return of(id, type, autoRegister) { item -> item.tag?.let { function(it, tagName) } }
        }

        /**
         * Creates a [DataType] that gets the tag named [tagName] from the item's Custom Data.
         *
         * See [getCompoundTagFunctionByType] for the allowed classes.
         */
        inline fun <reified T : Any> simple(id: String, tagName: String = id, autoRegister: Boolean = true): DataType<T> {
            return simple(id, typeOf<T>(), tagName, autoRegister)
        }
    }
}
